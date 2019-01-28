#!/bin/bash

oc login -u system:admin

oc adm policy add-cluster-role-to-user cluster-admin developer

oc login -u developer

oc new-project demo-monitoring

echo "Deploying a Data Grid cluster"

oc create -f https://raw.githubusercontent.com/jboss-container-images/jboss-datagrid-7-openshift-image/7.3-v1.0/services/datagrid-service-template.yaml

RHDG_USER=developer
RHDG_PASSWORD=developer

oc new-app datagrid-service \
  -p APPLICATION_USER=developer \
  -p APPLICATION_PASSWORD=developer \
  -e AB_PROMETHEUS_ENABLE=true

oc expose svc/datagrid-service

echo "Setting up the Prometheus Operator"

./install-prometheus.sh

echo "Open the Prometheus console at:"

oc get route prometheus

echo "Exposing Data Grid metrics to Prometheus"

oc apply -f service-metrics.yaml
oc expose svc/datagrid-app-metrics

oc apply -f service-monitor.yaml

echo "Setting up Grafana"

./install-grafana.sh

echo "Log in to Grafana (admin/admin) at:"

oc get route grafana
