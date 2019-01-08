#!/bin/bash


oc login -u system:admin

oc adm policy add-cluster-role-to-user cluster-admin developer
oc login -u developer

oc new-project demo-monitoring

read -n1 -r -p "Let's create Prometheus operator. Press any key to continue..." key

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

read -n1 -r -p "Now we'll deploy Datagrid cluster and connect it to Prometheus for monitoring. Press any key to continue...." key

echo "Installing RHDG cluster"

oc create -f datagrid73-basic.json

RHDG_USER=developer
RHDG_PASSWORD=developer

oc new-app datagrid73-basic \
  -p USERNAME=$RHDG_USER \
  -p PASSWORD=$RHDG_PASSWORD 

# Already exposed in template
# oc expose svc/datagrid-app

read -n1 -r -p "Now that Datagrid cluster is booting we'll expose Datagrid metrics to Prometheus. Press any key to continue..." key

oc apply -f 10-service-metrics.yaml
oc expose svc/datagrid-app-metrics

oc apply -f 11-datagrid-service-monitor.yaml 

read -n1 -r -p "Datagrid installation done! Not let's install Grafana. Press any key to continue..." key

oc new-app grafana/grafana:5.3.4
oc expose svc/grafana

read -n1 -r -p  "Let's wait a few moments for Grafana to boot. Now we'll install Prometheus data source in Grafana. Press any key to continue..." key

protocol="http://"
payload="$( mktemp )"
cat <<EOF >"${payload}"
{
"name": "Promethus",
"type": "prometheus",
"access": "Browser",
"url": "https://$( oc get route prometheus -n demo-monitoring -o jsonpath='{.spec.host}' )",
"basicAuth": false,
"withCredentials": false
}
EOF

# setup grafana data source
grafana_host="${protocol}$( oc get route grafana -o jsonpath='{.spec.host}' )"
dashboard_file="./infinispan-grafana-dashboard.json"
curl --insecure -H "Content-Type: application/json" -u admin:admin "${grafana_host}/api/datasources" -X POST -d "@${payload}"

# this does not work for some reason, import manualy
#curl --insecure -H "Content-Type: application/json" -u admin:admin "${grafana_host}/api/dashboards/import" -X POST -d "@${dashboard_file}"


echo "All done! Let's log into grafana (username/password is admin/admin) at: "
oc get route grafana

