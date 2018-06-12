#!/bin/bash

SCRIPT_DIR="$(dirname "${BASH_SOURCE[0]}")"

kcadm.sh config credentials --server http://localhost:8180/auth --realm master --user johndoe --client admin-cli

#Create realm demo
kcadm.sh create realms -s realm=demo -s enabled=true

#Create client
kcadm.sh create clients -r demo -f ${SCRIPT_DIR}/client-quickstart.json

#Create roles
kcadm.sh create roles -r demo -f ${SCRIPT_DIR}/role-admin.json
kcadm.sh create roles -r demo -f ${SCRIPT_DIR}/role-reader.json

#Create users
kcadm.sh create users -r demo -f ${SCRIPT_DIR}/user-admin.json
kcadm.sh create users -r demo -f ${SCRIPT_DIR}/user-reader.json

#Assign roles
kcadm.sh add-roles -r demo --uusername admin --rolename admin
kcadm.sh add-roles -r demo --uusername reader --rolename reader
