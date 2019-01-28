#!/bin/bash

oc apply -f 1-cluster-role-binding.yaml
oc apply -f 2-cluster-role.yaml
oc apply -f 3-prometheus-operator-deployment.yaml
oc apply -f 4-service-account.yaml

# Set permissions for prometheus and its operator
oc adm policy add-scc-to-user privileged -ndemo-monitoring -z prometheus-operator
oc adm policy add-scc-to-user privileged -ndemo-monitoring -z prometheus

read -n1 -r -p "Please wait for Prometheus operator to be deployed. Confirm deployment using 'oc get pods' from another terminal window. Once confirmed press any key to proceed with installation..." key

echo "Setting RBAC"

oc apply -f 5-rbac-service-account.yaml
oc apply -f 6-rbac-cluster-roles.yaml
oc apply -f 7-rbac-cluster-role-binding.yaml

echo "Creating service monitors and exposing Prometheus web console..."

oc apply -f 8-service-monitors.yaml
oc apply -f 9-expose-prometheus.yaml

oc create route edge  --service=prometheus
