#!/usr/bin/env bash

OPEN_CURLY="{"
external_ip=$(oc get svc/datagrid-service-xsite --template="$OPEN_CURLY{range .status.loadBalancer.ingress}}$OPEN_CURLY{.hostname}}$OPEN_CURLY{end}}")
counter=240
while [ x"$external_ip" == "x" ] || [ "<no value>" == "$external_ip" ]; do
   external_ip=$(oc get svc/datagrid-service-xsite --template="$OPEN_CURLY{range .status.loadBalancer.ingress}}$OPEN_CURLY{.hostname}}$OPEN_CURLY{end}}")
   if [ "<no value>" == "$external_ip" ]; then
      external_ip=$(oc get svc/datagrid-service-xsite --template="$OPEN_CURLY{range .status.loadBalancer.ingress}}$OPEN_CURLY{.ip}}$OPEN_CURLY{end}}")
   fi
   counter=$((counter-1))
   if [ "$counter" -eq "0" ];then
      external_ip="127.0.0.1"
      break
   fi
done
echo "$external_ip"
