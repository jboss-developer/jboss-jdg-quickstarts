#!/usr/bin/env bash

set -e -x

VMDRIVER=$1

minishift profile set datagrid-quickstart
minishift config set memory 8GB
minishift config set cpus 4
minishift config set disk-size 50g
minishift config set image-caching true

if [ -n "${VMDRIVER}" ]; then
    echo "Using VM driver '$VMDRIVER'"
    # Set virtual machine drivers.
    minishift config set vm-driver ${VMDRIVER}
fi


# Enable admin-user to delete projects.
minishift addon enable admin-user
