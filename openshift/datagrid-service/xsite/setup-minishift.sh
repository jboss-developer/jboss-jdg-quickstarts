#!/usr/bin/env bash

set -e -x

VMDRIVER=${1-virtualbox}

echo "Using VM driver '$VMDRIVER'"

minishift profile set xsite-a
minishift config set memory 4GB
minishift config set cpus 2
minishift config set disk-size 25g
minishift config set image-caching true
minishift config set vm-driver ${VMDRIVER}
# Enable admin-user addon to be able to remove projects
minishift addon enable admin-user

minishift profile set xsite-b
minishift config set memory 4GB
minishift config set cpus 2
minishift config set disk-size 25g
minishift config set image-caching true
minishift config set vm-driver ${VMDRIVER}
# Enable admin-user addon to be able to remove projects
minishift addon enable admin-user
