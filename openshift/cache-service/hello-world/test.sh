#!/usr/bin/env bash

set -e


stopService() {
  echo "--> Stop service"
  oc delete all,secrets,sa,templates,configmaps,daemonsets,clusterroles,rolebindings,serviceaccounts --selector=template=cache-service || true
  oc delete template cache-service || true
}


startService() {
  echo "--> Start service"

  # TODO last datagrid73-dev commit on 15.11.18
  oc create -f \
    https://raw.githubusercontent.com/jboss-container-images/jboss-datagrid-7-openshift-image/f186aba68a3042605e64daac706e7ca364b1c758/services/cache-service-template.yaml

  oc new-app cache-service \
    -p APPLICATION_USER=test \
    -p APPLICATION_USER_PASSWORD=changeme
}


waitForService() {
  echo "--> Wait for service"

  ready="false"
  while [ "$ready" != "true" ];
  do
    ready=`oc get pod -l application=cache-service -o jsonpath="{.items[0].status.containerStatuses[0].ready}"`
    status=`oc get pod -l application=cache-service -o jsonpath="{.items[0].status.containerStatuses[0].state}"`
    echo "Pod: ready=${ready},status=${status}"
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
  local demo=$1

  oc new-build \
    --binary \
    --strategy=source \
    --name=${demo} \
    -l app=${demo} \
    fabric8/s2i-java:2.3

  mvn -s ../../../settings.xml clean package compile -DincludeScope=runtime

  oc start-build ${demo} --from-dir=target/ --follow
}


startQuickstart() {
  echo "--> Start quickstart"
  local demo=$1

  oc run ${demo} \
    --image=`oc get is ${demo}  -o jsonpath="{.status.dockerImageRepository}"` \
    --replicas=1 \
    --restart=OnFailure \
    --env JAVA_OPTIONS=-ea
}


waitForQuickstart() {
  echo "--> Wait for quickstart"
  local demo=$1

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


main () {
  if [ "$1" == "--quickstart-only" ]; then
    stopService
    startService
    waitForService
  fi

  local demo="quickstart"

  stopQuickstart ${demo}
  buildQuickstart ${demo}
  startQuickstart ${demo}
  waitForQuickstart ${demo}
  logQuickstart ${demo}
}


main
