#!/usr/bin/env bash

set -e -x

minishift profile set openshift-datagrid-tutorials

minishift config set memory 8GB
minishift config set cpus 4
minishift config set disk-size 50g
minishift config set image-caching true

# TODO add as parameter for script
minishift config set vm-driver xhyve

# TODO are they needed?
# minishift addon enable admin-user
# minishift addon enable anyuid
