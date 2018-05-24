Web application: A web application use a cache provided by the EAP server
==========================================================================================
Author: Wolf-Dieter Fink
Level: Beginner
Technologies: Infinispan, EAP


What is it?
-----------

This example demonstrates how to use the JDG EAP modules and configure an EAP server to manage JDG caches.
The configuration is included within the EAP standalone.xml file. The application code will get a cache reference
by injection or lookup via JNDI. The caches are not exposed and only applications deployed on this EAP instance 
can use it.
The lifecycle of the cache is managed by the EAP instance, the applications can undeployed or deployed without affects to the cache.



Prepare the server instances
-------------
Simple start
1.  Create a fresh copy of EAP 7.1 (or newer) server

2.  Unzip jboss-datagrid-7.2.0-eap-modules-library.zip and copy the modules/* directories into the EAP_HOME/modules directory

3.  start EAP with the default configuration standalone.xml

        $EAP_HOME/bin/standalone.sh

4. use the CLI scripts to configure the datagrid subsystem and necessary dependencies

        $EAP_HOME/bin/jboss-cli.sh -c --file=$SRC_HOME/install1-standalone-local.cli
        > restart the server !
       $EAP_HOME/bin/jboss-cli.sh -c --file=$SRC_HOME/install2-standalone-local.cli


Build and Run the example
-------------------------
1. Type this command to build and deploy the archive:

        mvn clean package
        cp web/target/jboss-eap-datagrid-subsystem-AppWeb.war $EAP_HOME/standalone/deployments


Run the web application in your browser
=======================================

      http://localhost:8080/jboss-eap-datagrid-subsystem-AppWeb/

Add, delete and list entries ...

Run as a clustered cache
=====================================

The configuration for EAP is a not clustered one, but it is possible to use the install scripts for a *ha* configuration as well.
If the JDG caches should be clustered use the install?-standalone-clustered.cli installation scripts for the steps above instead.
In this case the JDG caches are clustered no matter whether the EAP instances are clustered or not.

To see the behaviour for JDG caches

1.  Create a fresh copy of EAP 7.1 (or newer) server

2.  Unzip jboss-datagrid-7.2.0-eap-modules-library.zip and copy the modules/* directories into the EAP_HOME/modules directory

3.  start EAP with the default configuration standalone.xml

        $EAP_HOME/bin/standalone.sh [-c standalone-ha.xml]

4. use the CLI scripts to configure the datagrid subsystem and necessary dependencies

        $EAP_HOME/bin/jboss-cli.sh -c --file=$SRC_HOME/install1-standalone-clustered.cli
        > restart the server !
       $EAP_HOME/bin/jboss-cli.sh -c --file=$SRC_HOME/install2-standalone-clustered.cli

5. Stop the server

6. Copy the web application to the deployment folder

        cp web/target/jboss-eap-datagrid-subsystem-AppWeb.war $EAP_HOME/standalone/deployments

7. Copy the EAP instance to another directory

8. Start both servers and use the property to change the JDG multicast address

        $EAP_HOME1/bin/standalone.sh [-c standalone-ha.xml] -Djdg.bind.address=<IP address>
        $EAP_HOME2/bin/standalone.sh [-c standalone-ha.xml] -Djdg.bind.address=<IP address> [-Djboss.socket.binding.port-offset=100]

9.  Access both web applications and check that the cache entries are distributed


Notes
=======
  - You can simple rename the jboss-eap-datagrid-subsystem-AppWeb.war to any other name.war to simulate different applications.
    If there are multiple applications they share the same cache!
  - Caches are independent from application lifecycles. Undeploying any, or all, applications instances has no affect on a cache's lifecycle or data.
    Even if the cache has no persistence the data will be kept as long as the server is not stopped.
  - In this example the EAP server is not clustered, but if the cache is configured as clustered with the datagrid-jgroups subsystem and replicated or 
    distributed caches, the cache will be synchronized between different EAP instances if configured correctly.
  - The respective JGroups subsystem needs to be defined for both EAP and JDG as the respective subsystems depend on different JGroups versions and provide different functionality.
  - Also it is indespensable that the communication addresses and port for the JGroups subsystems need to be different. This prevent from cross-talking
    which cause performance drawback, WARN message or error messages.


Run tests
========================
1. Run `mvn clean package` to build the project (from the root folder of this quickstart)

2. Prepare the server instances(following the steps at the part `Prepare the server instances`) and keep EAP server running

3. Run	`mvn test -Ptests-eap -DeapHome=$EAP_HOME`



Run tests in clustered cache mode
========================
1. Run `mvn clean package` to build the project (from the root folder of this quickstart)

2. Set variables
	$EAP_SERVER_ZIP_PATH - path to jboss-eap.zip
	$JDG_MODULES_ZIP_PATH - path to jboss-datagrid-eap-modules-library.zip

3. Prepare two EAP server instances(following the steps 1 to 7 at part `Run as a clustered cache`)
or run prepareTwoEAP.sh from working directory (this will prepare two EAPs)


4. Save path to EAPs directories as $EAP_HOME1 and $EAP_HOME2

5. Run	`mvn test -Ptests-eap-clustered -DeapHome1=$EAP_HOME1 -DeapHome2=$EAP_HOME2`