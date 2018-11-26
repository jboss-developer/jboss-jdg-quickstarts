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
  local discovery=$4

  minishift profile set ${profile}
  eval $(minishift oc-env)

  oc create -f xsite-datagrid-service-template.yaml

  oc create configmap datagrid-config --from-file=configuration

  oc new-app datagrid-service \
    -p APPLICATION_USER=test \
    -p APPLICATION_USER_PASSWORD=changeme \
    -e USER_CONFIG_MAP=true \
    -e JAVA_OPTS_APPEND="-Djboss.bind.ext_address=${extAddr} -Djboss.relay.site=${siteName} -Djboss.relay.global_cluster=${discovery}"
}

#startServiceSiteA() {
#  echo "--> Start service"
#
#  oc create -f xsite-service.yaml
#
#  local site="SiteA"
#  local extAddr=`./external-ip.sh`
#  local discovery="${extAddr}[55200]"
#
#  oc create -f xsite-datagrid-service-template.yaml
#
#  oc create configmap datagrid-config --from-file=configuration
#
#  oc new-app datagrid-service \
#    -p APPLICATION_USER=test \
#    -p APPLICATION_USER_PASSWORD=changeme \
#    -e USER_CONFIG_MAP=true \
#    -e JBOSS_HA_ARGS="-Djboss.bind.ext_address=${extAddr} -Djboss.relay.site=${site} -Djboss.relay.global_cluster=${discovery}"
#
#  # Check logs for message like:
#  # INFO Running ___ image, version ___ with user standalone.xml
#}


main () {
  stopService "xsite-a"
  stopService "xsite-b"

  startXSiteService "xsite-a"
  local extAddrSiteA=`./external-ip.sh`
  echo "$extAddrSiteA"

  startXSiteService "xsite-b"
  local extAddrSiteB=`./external-ip.sh`
  echo "$extAddrSiteB"

  echo "External addresses are: siteA=${extAddrSiteA},siteB=${extAddrSiteB}"

  local discovery="${extAddrSiteA}[55200],${extAddrSiteB}[55200]"

  startService "xsite-a" "SiteA" "${extAddrSiteA}" "${discovery}"
  startService "xsite-b" "SiteB" "${extAddrSiteB}" "${discovery}"
}


main
