#!/bin/bash

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

