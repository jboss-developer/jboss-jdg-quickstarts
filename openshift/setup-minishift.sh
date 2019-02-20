#!/usr/bin/env bash

set -e -x

VMDRIVER=${1-virtualbox}

echo "Using VM driver '$VMDRIVER'"

minishift profile set datagrid-quickstart

minishift config set memory 8GB
minishift config set cpus 4
minishift config set disk-size 50g
minishift config set image-caching true

minishift config set vm-driver ${VMDRIVER}

# Enable admin-user addon to be able to remove projects
minishift addon enable admin-user
