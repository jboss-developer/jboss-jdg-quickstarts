#!/bin/bash
#Defining colors for output
RED='\033[0;31m'
NO_COLOR='\033[0m'
GREEN='\033[0;33m'

case "$1" in
    --setup | --setup-domain)
      #Cleaning up directories from previous run
      echo "Removing directories from previous run ..."
      rm -rf setupOutput/
      mkdir setupOutput

      #Unzipping the EAP server to eap-server/ directory
      echo "Unzipping EAP Server ..."
      unzip -q ${EAP_SERVER_ZIP_PATH} -d setupOutput/eap-server
      export EAP_HOME=`cd setupOutput/eap-server/*;pwd`

      #Unzipping the JDG EAP Modules to jdg-modules directory
      echo "Unzipping JDG Modules ..."
      unzip -q ${JDG_MODULES_ZIP_PATH} -d setupOutput/jdg-modules
      export JDG_MODULES=`cd setupOutput/jdg-modules/*;pwd`

      echo "Copying the JDG modules to EAP Home directory"
      cp -a "${JDG_MODULES}/modules" "${EAP_HOME}"

      echo "Adding user to EAP"
      "${EAP_HOME}"/bin/add-user.sh -a -u quickuser -p quick-123

      echo "Building quickstart .."
      mvn clean install > setupOutput/mvn_install.log

      if [ "${1}" == "--setup" ]; #Starting in standalone mode otherwise in domain mode
      then
        echo "Copying the configurations from README file to EAP ..."

        echo "Copying EAP server to directories"
        cp -R "${EAP_HOME}" setupOutput/eap-server/server1
        export EAP_HOME1=`cd setupOutput/eap-server/server1;pwd`

        cp -R "${EAP_HOME}" setupOutput/eap-server/server2
        export EAP_HOME2=`cd setupOutput/eap-server/server2;pwd`

        cp -R "${EAP_HOME}" setupOutput/eap-server/server3
        export EAP_HOME3=`cd setupOutput/eap-server/server3;pwd`

        cp -R "${EAP_HOME}" setupOutput/eap-server/server4
        export EAP_HOME4=`cd setupOutput/eap-server/server4;pwd`


        echo "Running quckstart in Standalon mode. Starting all 4 instances of the server..."
        sh "${EAP_HOME1}/"bin/standalone.sh -Djboss.node.name=node1 > "${EAP_HOME1}/server.log" &
        sleep 10

        sh "${EAP_HOME2}"/bin/standalone.sh -Djboss.node.name=node2 -Djboss.socket.binding.port-offset=100 > "${EAP_HOME2}/server.log" &
        sleep 10

        sh "${EAP_HOME3}"/bin/standalone.sh -Djboss.node.name=node3 -Djboss.socket.binding.port-offset=200 -c standalone-ha.xml > "${EAP_HOME3}/server.log" &
        sleep 10

        sh "${EAP_HOME4}"/bin/standalone.sh -Djboss.node.name=node4 -Djboss.socket.binding.port-offset=300 -c standalone-ha.xml > "${EAP_HOME4}/server.log" &
        sleep 10

        echo "Adding the configuration for nodes (AppOne) to use EJB server-to-server invocation ..."
        sh "${EAP_HOME1}"/bin/jboss-cli.sh  -c --controller=localhost:9990 --file=install-appOne-standalone.cli > setupOutput/install_server_1.log &
        sleep 2
        sh "${EAP_HOME2}"/bin/jboss-cli.sh  -c --controller=localhost:10090 --file=install-appOne-standalone.cli > setupOutput/install_server_2.log &
        sleep 2
        sh "${EAP_HOME3}"/bin/jboss-cli.sh  -c --controller=localhost:10190 --file=install-appOne-standalone.cli > setupOutput/install_server_3.log &
        sleep 2
        sh "${EAP_HOME4}"/bin/jboss-cli.sh  -c --controller=localhost:10290 --file=install-appOne-standalone.cli > setupOutput/install_server_4.log &
        sleep 2

        echo "Copying the resources to EAP servers.."
        cp adminApp/ear/target/jboss-eap-application-adminApp.ear "${EAP_HOME1}"/standalone/deployments
        cp appOne/ear/target/jboss-eap-application-AppOne.ear "${EAP_HOME2}"/standalone/deployments
        cp appTwo/ear/target/jboss-eap-application-AppTwo.ear "${EAP_HOME3}"/standalone/deployments
        cp appTwo/ear/target/jboss-eap-application-AppTwo.ear "${EAP_HOME4}"/standalone/deployments

        echo "Waiting for 40 seconds until the servers are deployed..."
        sleep 40
      else
         echo "Running quckstart in Domain mode ..."
         sh "${EAP_HOME}"/bin/domain.sh > "${EAP_HOME}/server.log" &
         sleep 10

         echo "Applying the configuration for the quickstart, the domain will contain 4 nodes ..."
         "${EAP_HOME}"/bin/jboss-cli.sh -c --file=install-domain.cli > setupOutput/install.log

         echo "Waiting until the nodes are started for deploying the application ..."
         sleep 40 #Waiting for applications to start so that the next operation succeeds

         echo "Deploying application for domain mode..."
         "${EAP_HOME}"/bin/jboss-cli.sh -c --file=deploy-domain.cli > setupOutput/deploy.log
      fi
   ;;

   --run)
      echo -e "${GREEN}Running the Step1. Add values to App1 cache with the AdminApp and validate that they are replicated to the server instance of AppOne.
           Add a value to App2 cache, rollback the transaction and check that it is not added to the cache after rollback.
           The AdminServer and the AppOneServer are not configured as JBoss EAP cluster, only the Infinispan caches are configured by the application
           to communicate and replicate the caches.${NO_COLOR}"
      mvn -Dexec.mainClass=org.jboss.as.quickstarts.datagrid.eap.app.AdminClient exec:java

      echo -e "${GREEN}Running Step 2: Add values to App2 cache with the AdminApp and access AppOne to show that the EJB invocation is clustered and both AppTwo instances are used.
         Show that the JBoss EAP and Infinispan clusters are not related and the Infinispan cluster is able to use a different JGroups implementation as the JBoss EAP server.${NO_COLOR}"
      mvn -Dexec.mainClass=org.jboss.as.quickstarts.datagrid.eap.app.AppOneClient exec:java
   ;;

   --teardown)
      echo "Stopping all servers ..."
      #Getting process ID in case if the servers were started in Domain Mode and killing it:
      (ps -ef | grep 'Process Controller' | grep -v grep | awk '{print $2}' | xargs kill) > /dev/null 2>&1
      (ps -ef | grep 'Standalone' | grep -v grep | awk '{print $2}' | xargs kill) > /dev/null 2>&1

   ;;

   *)
      echo -e "${GREEN}Possible list of arguments is:"
      echo "--setup cleanup, sets up the EAP servers, starts them in standalone mode and installs quickstart configuration;"
      echo "--setup-domain sets up the EAP servers, starts them in domain mode and installs quickstart configuration. Use this argument with --setup argument;"
      echo "--run runs the quickstarts;"
      echo -e "--teardown stops the servers;${NO_COLOR}"
      exit 1;
esac

echo "Done!"
