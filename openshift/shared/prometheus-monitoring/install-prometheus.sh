#!/bin/bash

oc apply -f prometheus-operator/cluster-role-binding.yaml
oc apply -f prometheus-operator/cluster-role.yaml
oc apply -f prometheus-operator/prometheus-operator-deployment.yaml
oc apply -f prometheus-operator/service-account.yaml

# Set permissions for the Prometheus Operator
oc adm policy add-scc-to-user privileged -n demo-monitoring -z prometheus-operator
oc adm policy add-scc-to-user privileged -n demo-monitoring -z prometheus

read -n1 -r -p "Wait for the Prometheus Operator to start running. Open another terminal window and run the 'oc get pods -w' command. When you confirm that the Prometheus Operator pod is running, press any key to continue..." key

echo "Setting up RBAC for the Prometheus Operator"

oc apply -f prometheus-operator/rbac-service-account.yaml
oc apply -f prometheus-operator/rbac-cluster-roles.yaml
oc apply -f prometheus-operator/rbac-cluster-role-binding.yaml

echo "Creating service monitors and exposing the Prometheus console"

oc apply -f prometheus-operator/service-monitors.yaml
oc apply -f prometheus-operator/expose-prometheus.yaml

oc create route edge --service=prometheus
