#!/bin/bash

REST_HOST=`oc get route/datagrid-app -o jsonpath="{.spec.host}"`
PROMETHEUS_HOST=`oc get route/prometheus -o jsonpath="{.spec.host}"`

USER="developer"
PASSWORD="developer"


for i in {3000..4000}
do
   echo "Writing key $i"
curl \
  -u $USER:$PASSWORD \
  -H 'Content-type: text/plain' \
  -d 'value $i' \
  $REST_HOST/rest/default/$i
done


echo "Datagrid REST endpoint at http://$REST_HOST"
echo "Prometheus console at http://$PROMETHEUS_HOST"
