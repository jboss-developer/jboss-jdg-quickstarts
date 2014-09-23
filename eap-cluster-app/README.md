eap-cluster-app: Example how to use JDG cache from a EAP application
====================================================================
Author: Wolf-Dieter Fink
Level: Advanced
Technologies: Infinispan, CDI, EAP
Summary: Shows how to use Infinispan from a EAP application in embedded mode
Target Product: JDG
Product Versions: EAP 6.x, JDG 6.3
Source: <https://github.com/infinispan/jdg-quickstart>

What is it?
-----------

EAP-cluster-app demonstrates how to use a Infinispan Cache in embedded mode inside of a Java EE application
deployed at an EAP6 server.
There are three different applications do show:
- how to create and use a JDG clustered cache without having a EAP cluster
- An EAP cluster is independend from a JDG cluster
- One EAP instance can use different JDG caches which are member of different JDG clusters
- Programmatic cache configuration using the Infinispan API
- File based configuration (administration application App1Cache)
- Use CDI to inject the cache managers

Each application contains a embedded JDG cache which is accessed by stateless EJB's in the same application.
The different EAP servers, except AppTwo, are not clustered to show that an unclustered application can share the JDG cache.
There are two cache managers used which are not part of the same JDG cluster.
The AdminApp is able to access both caches and change the entries.
AppOne can only read the App1Cache and use a clustered EJB invocation to AppTwo to read from App2Cache.
AppTwo is deployed as a clustered EJB application and only read App2Cache.

All applications need to have a installed JDG 6.3 module extention for the EAP server which can be downloaded from the Red Hat portal.


System requirements
-------------------

All you need to build this project is Java 6.0 (Java SDK 1.6) or better, Maven 3.0 or better.

The application this project produces is designed to be run on Red Hat JBoss Enterprise Application Platform (EAP) 6.1 or later.

 
Configure Maven
---------------

If you have not yet done so, you must [Configure Maven](../../README.md#configure-maven) before testing the quickstarts.


Configure and start EAP instances in standanlone mode
-----------------------------------------------------

1. Prepare a copy of the EAP
   - unzip jboss-datagrid-6.3.0.Beta-eap-modules-library.zip
   - copy the modules to the server modules directory

        For Linux:   cp -a jboss-datagrid-6.3.0.Beta-eap-modules-library/modules/org $EAP_HOME/modules
        For Windows: xcopy /e/i/f jboss-datagrid-6.3.0.Beta-eap-modules-library/modules %EAP_NODE1_HOME%\modules

   - Add a user to each server for ejb access

        For Linux:   $EAP_NODE#_HOME/bin/add-user.sh -a -u quickuser -p quick-123
        For Windows: %EAP_NODE#_HOME%\bin\add-user.bat -a -u quickuser -p quick-123

2. Copy the prepared EAP server to 4 different directories EAP_NODE[1-4].
3. Open a command line for each of the 4 nodes and navigate to the root of the EAP server directory.
   The following shows the command line to start the different servers:

        For Linux:   $EAP_NODE1_HOME/bin/standalone.sh -Djboss.node.name=node1
                     $EAP_NODE2_HOME/bin/standalone.sh -Djboss.node.name=node2 -Djboss.socket.binding.port-offset=100
                     $EAP_NODE3_HOME/bin/standalone.sh -Djboss.node.name=node3 -Djboss.socket.binding.port-offset=200 -c standalone-ha.xml
                     $EAP_NODE4_HOME/bin/standalone.sh -Djboss.node.name=node4 -Djboss.socket.binding.port-offset=300 -c standalone-ha.xml
        For Windows: %EAP_NODE1_HOME%\bin\standalone.bat -Djboss.node.name=node1
                     %EAP_NODE2_HOME%\bin\standalone.sh -Djboss.node.name=node2 -Djboss.socket.binding.port-offset=100
                     %EAP_NODE3_HOME%\bin\standalone.sh -Djboss.node.name=node3 -Djboss.socket.binding.port-offset=200 -c standalone-ha.xml
                     %EAP_NODE4_HOME%\bin\standalone.sh -Djboss.node.name=node4 -Djboss.socket.binding.port-offset=300 -c standalone-ha.xml


4. Add the configuration for node2 (AppOne) to use EJB server-to-server invocation:

        For Linux:   $EAP_NODE2_HOME/bin/jboss-cli.sh -c --file=$QUICKSTART_HOME/install-appOne-standalone.cli
        For Windows: %EAP_NODE2_HOME%\bin\jboss-cli.bat -c --file=%QUICKSTART_HOME%/install-appOne-standalone.cli

5. Copy the application to the appropriate server

        cp adminApp/ear/target/jboss-eap-application-adminApp.ear EAP_NODE1/standalone/deployments
        cp cp appOne/ear/target/jboss-eap-application-AppOne.ear EAP_NODE2/standalone/deployments
        cp cp appTwo/ear/target/jboss-eap-application-AppTwo.ear EAP_NODE3/standalone/deployments
        cp appTwo/ear/target/jboss-eap-application-AppTwo.ear EAP_NODE4/standalone/deployments


Configure and start EAP in domain mode
--------------------------------------

1. Copy a fresh EAP installation to $EAP_HOME
2. Open a command line and navigate to the root of the EAP directory.
3. Add a user:

        For Linux:   $EAP_HOME/bin/add-user.sh -a -u quickuser -p quick-123
        For Windows: %EAP_HOME%\bin\add-user.bat -a -u quickuser -p quick-123

4. The following shows the command line to start the domain:

        For Linux:   $EAP_HOME/bin/domain.sh
        For Windows: %EAP_HOME%\bin\domain.bat

5. Apply the configuration for the quickstart, the domain will contain 4 nodes:

        For Linux:   $EAP_HOME/bin/jboss-cli.sh -c --file=$QUICKSTART_HOME/install-domain.cli
        For Windows: %EAP_HOME%\bin\jboss-cli.bat -c --file=%QUICKSTART_HOME%/install-domain.cli



Build the Application
------------------------------------------------

_NOTE: The following build command assumes you have configured your Maven user settings. If you have not, you must include Maven setting arguments on the command line. See [Build and Deploy the Quickstarts](../README.md#build-and-deploy-the-quickstarts) for complete instructions and additional options._

1. Open a command line and navigate to the root directory of this quickstart.
2. Type this command to build and deploy the archive:

        mvn clean install
        
3. Copy the application to the appropriate server

        For Linux:   cp adminApp/ear/target/jboss-eap-application-adminApp.ear EAP_NODE1/standalone/deployments
                     cp appOne/ear/target/jboss-eap-application-AppOne.ear EAP_NODE2/standalone/deployments
                     cp appTwo/ear/target/jboss-eap-application-AppTwo.ear EAP_NODE3/standalone/deployments
                     cp appTwo/ear/target/jboss-eap-application-AppTwo.ear EAP_NODE4/standalone/deployments
        For Windows: copy adminApp\ear\target\jboss-eap-application-adminApp.ear %EAP_NODE1%\standalone\deployments
                     copy appOne\ear\target\jboss-eap-application-AppOne.ear %EAP_NODE2%\standalone\deployments
                     copy appTwo\ear\target\jboss-eap-application-AppTwo.ear %EAP_NODE3%\standalone\deployments
                     copy appTwo\ear\target\jboss-eap-application-AppTwo.ear %EAP_NODE4%\standalone\deployments
 
6. Deploy the applications to the domain:

        For Linux:   $EAP_HOME/bin/jboss-cli.sh -c --file=$QUICKSTART_HOME/deploy-domain.cli
        For Windows: %EAP_HOME%\bin\jboss-cli.bat -c --file=%QUICKSTART_HOME%/deploy-domain.cli



Access the application
----------------------

To start the different following applications you need to navigate to the client directory of this quickstart.


Step 1: Add values to App1 cache with the AdminApp and validate that they are replicated to the server instance of AppOne.
        Add a value to App2 cache, rollback the transaction and check that it is not added to the cache after rollback.
        The AdminServer and the AppOneServer are not configured as EAP cluster, only the JDG caches are configured by the application
        to communicate and replicate the caches.

        mvn -Dexec.mainClass=org.jboss.as.quickstarts.datagrid.eap.app.AdminClient exec:java

To add more instances it is possible to overwrite the used connections by adding the following argument

        mvn -Dexec.args="AdminHost AdminPort AppOneHost AppOnePort" ....

The defaults are "locahost 4447 localhost 4547"

  The console output show the actions and results, any unexpected result will show an Exception 
         Add a value to App1Cache with the AdminApp and check on the same instance that the value is correct added
           success
         Check the previous added value of App1Cache by accessing the AppOne Server
           success
         Add a value to App2Cache and check on the same instance that the value is correct added
           success
         Check whether changes to a cache are rollbacked if the transaction fail
           The cache App2 work as expected on rollback

Step 2: Add values to App2 cache with the AdminApp and acess AppOne to show that the EJB invocation is clustered and both AppTwo instances are used.
        Show that the EAP and JDG clusters are not related and the JDG cluster is able to use a different JDG/JGroups implementation
        as the EAP server.

        mvn -Dexec.mainClass=org.jboss.as.quickstarts.datagrid.eap.app.AppOneClient exec:java

  The console output show the actions and results, any unexpected result will show an Exception 
        Add a value to App2Cache with the AdminApp
        Access the App2Cache from the AppOneServer by using the clustered EJB@AppTwoServer
          success : received the following node names for EJB invocation : [node3, node4]




Debug the Application
---------------------

If you want to debug the source code or look at the Javadocs of any library in the project, run either of the following commands to pull them into your local repository. The IDE should then detect them.

        mvn dependency:sources
        mvn dependency:resolve -Dclassifier=javadoc

