#!/usr/bin/env bash

set -e


if [ -z "${REDHAT_REGISTRY_USER}" ]; then
   echo "Env variable REDHAT_REGISTRY_USER, which sets username for registry.redhat.io, is unset or set to the empty string"
   exit 1
fi

if [ -z "${REDHAT_REGISTRY_PASSWORD}" ]; then
   echo "Env variable REDHAT_REGISTRY_PASSWORD, which sets password for registry.redhat.io, is unset or set to the empty string"
   exit 1
fi
