#!/bin/bash

echo "Installing RHDG cluster"

oc create -f https://raw.githubusercontent.com/jboss-container-images/jboss-datagrid-7-openshift-image/datagrid73-dev/services/datagrid-service-template.yaml

RHDG_USER=developer
RHDG_PASSWORD=developer

oc new-app datagrid-service \
  -p APPLICATION_USER=developer \
  -p APPLICATION_PASSWORD=developer \
  -e AB_PROMETHEUS_ENABLE=true \
  -p IMAGE=brew-pulp-docker01.web.prod.ext.phx2.redhat.com:8888/jboss-datagrid-7/datagrid73-openshift:jb-datagrid-7.3-openshift-dev-rhel-7-containers-candidate-46261-20181219020350

oc expose svc/datagrid-service

read -n1 -r -p "Now that Datagrid cluster is booting we'll expose Datagrid metrics to Prometheus. Press any key to continue..." key

oc apply -f 10-service-metrics.yaml
oc expose svc/datagrid-app-metrics

oc apply -f 11-datagrid-service-monitor.yaml 


