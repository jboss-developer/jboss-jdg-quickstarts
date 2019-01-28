#!/bin/bash


oc login -u system:admin

oc adm policy add-cluster-role-to-user cluster-admin developer
oc login -u developer

oc new-project demo-monitoring


oc apply -f 1-cluster-role-binding.yaml
oc apply -f 2-cluster-role.yaml
oc apply -f 3-prometheus-operator-deployment.yaml
oc apply -f 4-service-account.yaml

# Set permissions for prometheus and its operator
oc adm policy add-scc-to-user privileged -ndemo-monitoring -z prometheus-operator
oc adm policy add-scc-to-user privileged -ndemo-monitoring -z prometheus

echo "Waiting 10 seconds for Prometheus to deploy"
sleep 10

echo "Setting RBAC"

oc apply -f 5-rbac-service-account.yaml
oc apply -f 6-rbac-cluster-roles.yaml
oc apply -f 7-rbac-cluster-role-binding.yaml

echo "Creating service monitors and exposing Prometheus web console..."

oc apply -f 8-service-monitors.yaml
oc apply -f 9-expose-prometheus.yaml

oc create route edge --service=prometheus

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

echo "Now that Datagrid cluster is booting we'll expose Datagrid metrics to Prometheus. Wait 10 sec for Datagrid to boot..."

sleep 10

oc apply -f 10-service-metrics.yaml
oc expose svc/datagrid-app-metrics

oc apply -f 11-datagrid-service-monitor.yaml 


echo "Setting up grafana"

oc new-app grafana/grafana:5.3.4
oc expose svc/grafana

echo "Let's wait a few moments for Grafana to boot. Now we'll install Prometheus data source in Grafana...."
sleep 10

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

echo "All done! Let's log into grafana (username/password is admin/admin) at: "
oc get route grafana

