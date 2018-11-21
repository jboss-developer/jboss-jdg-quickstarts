#!/usr/bin/env bash

set -e


stopService() {
  echo "--> Stop service"
  oc delete all,secrets,sa,templates,configmaps,daemonsets,clusterroles,rolebindings,serviceaccounts --selector=template=datagrid-service || true
  oc delete configmap datagrid-config || true
  oc delete template datagrid-service || true
}


startService() {
  echo "--> Start service"

  oc create -f user-datagrid-service-template.yaml

  oc create configmap datagrid-config --from-file=configuration

  oc new-app datagrid-service \
    -p APPLICATION_USER=test \
    -p APPLICATION_USER_PASSWORD=changeme \
    -e USER_CONFIG_MAP=true

  # Check logs for message like:
  # INFO Running ___ image, version ___ with user standalone.xml
}


main () {
  stopService
  startService
}


main
