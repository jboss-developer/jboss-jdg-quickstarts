#!/usr/bin/env bash

set -e

readonly PROGNAME=$(basename $0)
readonly ARGS="$@"
SERVICE_NAME="datagrid-service"
NUM_INSTANCES=2

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
            --clean) args="${args}-c " ;;
            --help) args="${args}-h " ;;
            --quickstart-only) args="${args}-q " ;;
            --debug) args="${args}-x " ;;
        #pass through anything else
            *) [[ "${arg:0:1}" == "-" ]] || delim="\""
            args="${args}${delim}${arg}${delim} " ;;
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


stopService() {
    echo "--> Stop service"
    oc delete all,secrets,sa,templates,configmaps,daemonsets,clusterroles,rolebindings,serviceaccounts --selector=template=${SERVICE_NAME} || true
    oc delete configmap datagrid-config || true
    oc delete template ${SERVICE_NAME} || true
}


startService() {
    local appName=$1
    echo "--> Start service for ${appName}"

    oc create -f user-datagrid-service-template.yaml

    oc create configmap datagrid-config --from-file=configuration

    oc new-app datagrid-service \
        -p APPLICATION_NAME=${appName} \
        -p NUMBER_OF_INSTANCES=${NUM_INSTANCES} \
        -e USER_CONFIG_MAP=true

    # Check logs for message like:
    # INFO Running ___ image, version ___ with user standalone.xml
}


waitForClusterToForm() {
    local appName=$1
    local expectedClusterSize=${NUM_INSTANCES}
    local connectCmd="oc exec -it ${appName}-0 -- /opt/datagrid/bin/cli.sh --connect"
    local clusterSizeCmd="/subsystem=datagrid-infinispan/cache-container=clustered/:read-attribute(name=cluster-size)"

    local members=''
    while [ "$members" != \"${expectedClusterSize}\" ];
    do
        members=$(${connectCmd} ${clusterSizeCmd} | grep result | tr -d '\r' | awk '{print $3}')
        echo "Waiting for clusters to form (main: $members)"
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


clean() {
    echo "--> Stopa and clean service and quickstart"
    local demo=$1

    stopService
    stopQuickstart ${demo}
}


restartService() {
    local appName=$1
    stopService
    startService ${appName}
    waitForClusterToForm ${appName}
    echo "Cluster formed"
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


runQuickstart() {
    echo "--> Run quickstart"
    local demo=$1
    local appName=$2

    buildQuickstart
    stopQuickstart ${demo}
    uploadQuickstart ${demo}

    local svcDnsName="${appName}-hotrod"

    startQuickstart ${demo} ${svcDnsName}
    waitForQuickstart ${demo}
    logQuickstart ${demo}
}


startQuickstart() {
    local demo=$1
    local svcDnsName=$2
    echo "--> Start quickstart for dns name ${svcDnsName}"

    oc run ${demo} \
        --image=`oc get is ${demo} -o jsonpath="{.status.dockerImageRepository}"` \
        --replicas=1 \
        --restart=OnFailure \
        --env SVC_DNS_NAME=${svcDnsName} \
        --env JAVA_OPTIONS=-ea
}


waitForQuickstart() {
    echo "--> Wait for quickstart"
    local demo=$1

    sleep .5 # Short sleep before first try

    status=NA
    while [ "$status" != "Running" ];
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


runHttpRest() {
    local appName=$1
    echo "--> Run HTTP REST on ${appName}"

    echo "--> Store data with HTTP POST"
    oc exec -it ${appName}-0 \
        -- curl -v -X POST -H 'Content-type: text/plain' -d 'user-config' ${appName}-http:8080/rest/default/key-rest

    echo "--> Get data with HTTP GET"
    oc exec -it ${appName}-0 \
        -- curl -v ${appName}-http:8080/rest/default/key-rest
}


main() {
    cmdline $ARGS

    local demo="quickstart"

    local appName="${SERVICE_NAME}-user-config"
    local svcDnsName="${appName}-hotrod"

    echo "--> Test params: service=${SERVICE_NAME},app=${appName}";

    if [ -n "${CLEAN+1}" ]; then
        clean ${demo}
    else
        if [ -z "${QUICKSTART_ONLY+1}" ]; then
            restartService ${appName}
        fi

        runQuickstart ${demo} ${appName}
        runHttpRest ${appName}
    fi
}


main
