#!/usr/bin/env bash

set -e

readonly PROGNAME=$(basename $0)
readonly ARGS="$@"
SERVICE_NAME="datagrid-service"

usage() {
    cat <<- EOF
	usage: $PROGNAME options

	Builds, deploys and tests the quickstart and associated services on OpenShift.

	OPTIONS:
		-c --clean               delete the services and quickstart from OpenShift.
		-h --help                show this help.
		-q --quickstart-only     only test the quickstart, it assumes the service is running.
		-x --debug               debug


	Examples:
		Test quickstart on $SERVICE_NAME:
		$PROGNAME

		Only tests quickstart, assumes service is already running:
		$PROGNAME --quickstart-only

		Clean and remove quickstart and $SERVICE_NAME deployment:
		$PROGNAME --clean

		Run with extra logging:
		$PROGNAME --debug
	EOF
}

cmdline() {
    local arg=
    for arg
    do
        local delim=""
        case "$arg" in
        #translate --gnu-long-options to -g (short options)
            --clean)                   args="${args}-c ";;
            --help)                    args="${args}-h ";;
            --quickstart-only)         args="${args}-q ";;
            --debug)                   args="${args}-x ";;
        #pass through anything else
            *) [[ "${arg:0:1}" == "-" ]] || delim="\""
            args="${args}${delim}${arg}${delim} ";;
        esac
    done

    #Reset the positional parameters to the short options
    eval set -- $args

    while getopts "chqx" OPTION
    do
        case $OPTION in
            c)
                readonly CLEAN=1
            ;;
            h)
                usage
                exit 0
            ;;
            q)
                readonly QUICKSTART_ONLY=1
            ;;
            x)
                readonly DEBUG='-x'
                set -x
            ;;
        esac
    done

    return 0
}


switchProfile() {
  local profile=$1

  minishift profile set ${profile}
  eval "$(minishift oc-env)"
}


stopService() {
    echo "--> Stop service"
    oc delete all,secrets,sa,templates,configmaps,daemonsets,clusterroles,rolebindings,serviceaccounts --selector=template=datagrid-service || true
    oc delete configmap datagrid-config || true
    oc delete template datagrid-service || true
    oc delete service datagrid-service-xsite || true
}

startXSiteService() {
    oc create -f xsite-service.yaml
}

startService() {
    echo "-->" $FUNCNAME $@
    local siteName=$1
    local extAddr=$2
    local extPort=$3
    local discovery=$4
    local appName=$5

    oc create -f xsite-datagrid-service-template.yaml

    oc create configmap datagrid-config --from-file=configuration

    oc new-app datagrid-service \
        -p APPLICATION_NAME=${appName} \
        -p APPLICATION_USER=test \
        -p APPLICATION_PASSWORD=changeme \
        -e USER_CONFIG_MAP=true \
        -e SCRIPT_DEBUG=true \
        -e JAVA_OPTS_APPEND="-Djboss.bind.ext_address=${extAddr} -Djboss.bind.ext_port=${extPort} -Djboss.relay.site=${siteName} -Djboss.relay.global_cluster=${discovery}"
}


# TODO Should work once JDG-2592 fixed
#waitForXSiteViewToForm() {
#    local expectedXSiteABView="[SiteA,SiteB]";
#    local expectedXSiteBAView="[SiteB,SiteA]";
#    local connectCmd="oc exec -it datagrid-service-0 -- /opt/datagrid/bin/ispn-cli.sh --connect"
#    local xsiteViewCmd="/subsystem=datagrid-infinispan/cache-container=clustered/:read-attribute(name=sites-view)"
#
#    local members=''
#    while [[ "$members" != "$expectedXSiteABView" && "$members" != "$expectedXSiteBAView" ]];
#    do
        members=$(${connectCmd} ${xsiteViewCmd} | grep result | tr -d '\r' | awk '{print $3 $4}' | tr -d '"')
#        echo "Waiting for clusters to form, x-site view: $members"
#        sleep 10
#    done
#}

waitForXSiteViewToForm() {
    local appName=$1
    local expectedXSiteABView="[SiteA,SiteB]";
    local expectedXSiteBAView="[SiteB,SiteA]";

    local members=''
    while [[ "$members" != "$expectedXSiteABView" && "$members" != "$expectedXSiteBAView" ]];
    do
        members=$(oc logs ${appName}-0 | grep "Received new x-site view" | awk '{print $10 $11}')
        echo "Waiting for clusters to form, x-site view: $members"
        sleep 10
    done
}


stopQuickstart() {
    echo "--> Stop quickstart"
    local demo=$1

    oc delete all --selector=run=${demo} || true
    oc delete imagestream quickstart || true
    oc delete buildconfig quickstart || true
}


buildQuickstart() {
    mvn -s ../../../settings.xml clean package compile -DincludeScope=runtime
}


uploadQuickstart() {
    local demo=$1

    oc new-build \
        --binary \
        --strategy=source \
        --name=${demo} \
        -l app=${demo} \
        fabric8/s2i-java:2.3

    oc start-build ${demo} --from-dir=target/ --follow
}

startQuickstart() {
    local demo=$1
    local svcDnsName=$2
    local cmd=$3
    echo "--> Start quickstart for dns name ${svcDnsName} and command ${cmd}"

    oc run ${demo} \
        --image=`oc get is ${demo} -o jsonpath="{.status.dockerImageRepository}"` \
        --replicas=1 \
        --restart=OnFailure \
        --env SVC_DNS_NAME=${svcDnsName} \
        --env CMD=${cmd} \
        --env JAVA_OPTIONS=-ea
}

waitForQuickstart() {
    echo "--> Wait for quickstart"
    local demo=$1

    status=NA
    while [[ "$status" != "Running" && "$status" != "Succeeded" ]];
    do
        status=`oc get pod -l run=${demo} -o jsonpath="{.items[0].status.phase}"`
        echo "Status of pod: ${status}"
        sleep .5
    done
}

logQuickstart() {
    echo "--> Log quickstart"
    local demo=$1

    local pod=`oc get pod -l run=${demo} -o jsonpath="{.items[0].metadata.name}"`
    oc logs ${pod} -f
}

clean() {
    echo "--> Stop and clean service and quickstart"
    local demo=$1

    switchProfile "xsite-a"
    stopService
    stopQuickstart ${demo}

    switchProfile "xsite-b"
    stopService
    stopQuickstart ${demo}
}

restartService() {
    local appName=$1

    echo "--> Restart service";
    switchProfile "xsite-a"
    stopService

    switchProfile "xsite-b"
    stopService

    switchProfile "xsite-a"
    startXSiteService
    local extAddrSiteA=$(minishift ip)
    local extPortSiteA=`oc get svc/datagrid-service-xsite --template="{{range .spec.ports}}{{.nodePort}}{{end}}"`
    echo "External info for SiteA: $extAddrSiteA:$extPortSiteA"

    switchProfile "xsite-b"
    startXSiteService
    local extAddrSiteB=$(minishift ip)
    local extPortSiteB=`oc get svc/datagrid-service-xsite --template="{{range .spec.ports}}{{.nodePort}}{{end}}"`
    echo "External info for SiteA: $extAddrSiteB:$extPortSiteB"

    local discovery="${extAddrSiteA}[${extPortSiteA}],${extAddrSiteB}[${extPortSiteB}]"

    switchProfile "xsite-a"
    startService "SiteA" "${extAddrSiteA}" "${extPortSiteA}" "${discovery}" ${appName}

    sleep 30 # give some time for first site to come up

    switchProfile "xsite-b"
    startService "SiteB" "${extAddrSiteB}" "${extPortSiteB}" "${discovery}" ${appName}

    waitForXSiteViewToForm ${appName}
    echo "X-Site view formed"
}

runQuickstart() {
    echo "--> Run quickstart"
    local demo=$1
    local appName=$2

    buildQuickstart

    switchProfile "xsite-a"
    stopQuickstart ${demo}
    uploadQuickstart ${demo}

    switchProfile "xsite-b"
    stopQuickstart ${demo}
    uploadQuickstart ${demo}

    switchProfile "xsite-a"
    startQuickstart ${demo} ${appName} "put-data"
    waitForQuickstart ${demo}
    logQuickstart ${demo}

    switchProfile "xsite-b"
    startQuickstart ${demo} ${appName} "get-data"
    waitForQuickstart ${demo}
    logQuickstart ${demo}
}

main() {
    cmdline $ARGS

    local demo="quickstart"

    local appName="${SERVICE_NAME}-xsite-hello-world"

    echo "--> Test params: service=${SERVICE_NAME},app=${appName}";

    if [ -n "${CLEAN+1}" ]; then
        clean ${demo}
    else
        if [ -z "${QUICKSTART_ONLY+1}" ]; then
            restartService ${appName}
        fi

        runQuickstart ${demo} ${appName}
    fi
}

main
