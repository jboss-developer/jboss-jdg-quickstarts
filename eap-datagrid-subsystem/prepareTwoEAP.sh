#!/bin/bash

export SRC_HOME=`pwd`

echo "Removing directories from previous run ..."
rm -rf eap-server
rm -rf jdg-modules
rm -rf eap1
rm -rf eap2

echo "Unzipping EAP server ..."
unzip -q ${EAP_SERVER_ZIP_PATH} -d eap-server
export EAP_HOME=`cd eap-server/*;pwd`
echo $EAP_HOME

echo "Unzipping JDG eap modules library ..."
unzip -q ${JDG_MODULES_ZIP_PATH} -d jdg-modules
export JDG_MODULES=`cd jdg-modules/*;pwd`
echo $JDG_MODULES

echo "Copying the JDG modules to EAP home directory"
cp -a "${JDG_MODULES}/modules" "${EAP_HOME}"

echo "Starting EAP server"
sh "${EAP_HOME}"/bin/standalone.sh > "${EAP_HOME}/server.log" &
sleep 10

echo "Executing install1-standalone-clustered.cli console scripts"
"${EAP_HOME}"/bin/jboss-cli.sh -c --file="${SRC_HOME}"/install1-standalone-clustered.cli
sleep 2

echo "Restarting EAP server"
(ps -ef | grep 'Standalone' | grep -v grep | awk '{print $2}' | xargs kill) > /dev/null 2>&1
sleep 2
sh "${EAP_HOME}"/bin/standalone.sh > "${EAP_HOME}/server.log" &
sleep 10

echo "Executing install2-standalone-clustered.cli console scripts"
"${EAP_HOME}"/bin/jboss-cli.sh -c --file="${SRC_HOME}"/install2-standalone-clustered.cli
sleep 5

echo "Stopping EAP server"
(ps -ef | grep 'Standalone' | grep -v grep | awk '{print $2}' | xargs kill) > /dev/null 2>&1

echo "Making the first copy of EAP server ..."
cp -R "${EAP_HOME}" eap1
export EAP_HOME1=`cd eap1;pwd`

echo "Making the second copy of EAP server ..."
cp -R "${EAP_HOME}" eap2
export EAP_HOME1=`cd eap2;pwd`
