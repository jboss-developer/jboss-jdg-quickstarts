#!/usr/bin/env bash

set -e


stopService() {
  local profile=$1

  minishift profile set ${profile}
  eval $(minishift oc-env)
  
  echo "--> Stop service"
  oc delete all,secrets,sa,templates,configmaps,daemonsets,clusterroles,rolebindings,serviceaccounts --selector=template=datagrid-service || true
  oc delete configmap datagrid-config || true
  oc delete template datagrid-service || true
  oc delete service datagrid-service-xsite || true
}

startXSiteService() {
  local profile=$1
  echo "--> Start x-site service for ${profile}"

  minishift profile set ${profile}
  eval $(minishift oc-env)

  oc create -f xsite-service.yaml
}

startService() {
  echo "-->" $FUNCNAME $@
  local profile=$1
  local siteName=$2
  local extAddr=$3
  local extPort=$4
  local discovery=$5

  minishift profile set ${profile}
  eval $(minishift oc-env)

  oc create -f xsite-datagrid-service-template.yaml

  oc create configmap datagrid-config --from-file=configuration

  oc new-app datagrid-service \
    -p APPLICATION_USER=test \
    -p APPLICATION_USER_PASSWORD=changeme \
    -e USER_CONFIG_MAP=true \
    -e SCRIPT_DEBUG=true \
    -e JAVA_OPTS_APPEND="-Djboss.bind.ext_address=${extAddr} -Djboss.bind.ext_port=${extPort} -Djboss.relay.site=${siteName} -Djboss.relay.global_cluster=${discovery}"
}

main () {
  stopService "xsite-a"
  stopService "xsite-b"

  startXSiteService "xsite-a"
  local extAddrSiteA=$(minishift ip)
  local extPortSiteA=`oc get svc/datagrid-service-xsite --template="{{range .spec.ports}}{{.nodePort}}{{end}}"`
  echo "External info for SiteA: $extAddrSiteA:$extPortSiteA"

  startXSiteService "xsite-b"
  local extAddrSiteB=$(minishift ip)
  local extPortSiteB=`oc get svc/datagrid-service-xsite --template="{{range .spec.ports}}{{.nodePort}}{{end}}"`
  echo "External info for SiteB: $extAddrSiteB:$extPortSiteB"

  local discovery="${extAddrSiteA}[${extPortSiteA}],${extAddrSiteB}[${extPortSiteB}]"

  startService "xsite-a" "SiteA" "${extAddrSiteA}" "${extPortSiteA}" "${discovery}"

  sleep 30 # give some time for first site to come up

  startService "xsite-b" "SiteB" "${extAddrSiteB}" "${extPortSiteB}" "${discovery}"
}


main
