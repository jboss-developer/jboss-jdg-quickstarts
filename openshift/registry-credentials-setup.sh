#!/usr/bin/env bash

set -e


oc delete secret registry.redhat.io || true

oc create secret docker-registry registry.redhat.io \
  --docker-server=registry.redhat.io \
  --docker-username=$REDHAT_REGISTRY_USER \
  --docker-password=$REDHAT_REGISTRY_PASSWORD \
  --docker-email=unused
oc secrets link builder registry.redhat.io
oc secrets link default registry.redhat.io --for=pull
