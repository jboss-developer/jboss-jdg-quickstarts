#!/usr/bin/env bash

set -e

checkRegistryCredentials() {
   if [ -z "${REDHAT_REGISTRY_USER}" ]; then
       echo "Env variable REDHAT_REGISTRY_USER, which sets username for registry.redhat.io, is unset or set to the empty string"
   fi

   if [ -z "${REDHAT_REGISTRY_PASSWORD}" ]; then
       echo "Env variable REDHAT_REGISTRY_PASSWORD, which sets password for registry.redhat.io, is unset or set to the empty string"
   fi
}


startMinishift() {
   minishift start
   eval $(minishift oc-env)
}


setupRegistryAccess() {
   local usr=$REDHAT_REGISTRY_USER
   local pwd=$REDHAT_REGISTRY_PASSWORD

   oc create secret docker-registry registry.redhat.io \
     --docker-server=registry.redhat.io \
     --docker-username=$usr \
     --docker-password=$pwd \
     --docker-email=unused
   oc secrets link builder registry.redhat.io
   oc secrets link default registry.redhat.io --for=pull
}


main () {
   checkRegistryCredentials
   startMinishift
   setupRegistryAccess
}


main
