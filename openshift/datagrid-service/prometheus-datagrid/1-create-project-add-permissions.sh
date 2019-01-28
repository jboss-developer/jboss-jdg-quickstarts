#!/bin/bash


oc login -u system:admin

oc adm policy add-cluster-role-to-user cluster-admin developer
oc login -u developer

oc new-project demo-monitoring

