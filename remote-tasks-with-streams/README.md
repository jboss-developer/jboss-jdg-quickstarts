remote-tasks-with-streams: Example Using Remote Task Execution over HotRod Connector
=========================================
Author: Anna Manukyan  
Level: Intermediate  
Technologies: Infinispan, Hot Rod  
Summary: The `remote-tasks-with-streams` quickstart demonstrates how to execute remote tasks over the Hot Rod protocol with streams usage.  
Target Product: JDG  
Product Versions: JDG 7.x  
Source: <https://github.com/infinispan/jdg-quickstart>  

What is it?
-----------

Hot Rod is a binary TCP client-server protocol used in JBoss Data Grid. The Hot Rod protocol facilitates faster client and server interactions in comparison to other text based protocols and allows clients to make decisions about load balancing, failover and data location operations.

This quickstart demonstrates how to connect remotely to JBoss Data Grid (JDG) to store, retrieve, and remove data from cache using the Hot Rod protocol over remote tasks execution.
It is a simple Library Manager console application allows you to add and remove books, print a list of the all available books sorted alphabetically by title,
as well as to query over the existing books according to inserted parameters.


System requirements
-------------------

All you need to build this project is Java 8.0 (Java SDK 1.8) or better, Maven 3.0 or better.

The application this project produces is designed to be run on JBoss Data Grid 7.x


Configure Maven
---------------

If you have not yet done so, you must [Configure Maven](https://github.com/jboss-developer/jboss-developer-shared-resources/blob/master/guides/CONFIGURE_MAVEN.md#configure-maven-to-build-and-deploy-the-quickstarts) before testing the quickstarts.


Configure JDG
-------------

1. Obtain JDG server distribution on Red Hat's Customer Portal at https://access.redhat.com/jbossnetwork/restricted/listSoftware.html

2. Alter the JDG configuration file (`JDG_HOME/standalone/configuration/standalone.xml`) to contain the following definition:

        <subsystem xmlns="urn:infinispan:server:core:8.5" default-cache-container="local">
            <cache-container name="local" default-cache="default">
                <local-cache name="default" start="EAGER">
                    <locking acquire-timeout="30000" concurrency-level="1000" striping="false"/>
                </local-cache>
                <local-cache name="memcachedCache" start="EAGER">
                    <locking acquire-timeout="30000" concurrency-level="1000" striping="false"/>
                </local-cache>
                <local-cache name="namedCache" start="EAGER"/>

                <!-- ADD a local cache called 'library' -->

                <local-cache name="library" start="EAGER">
                    <compatibility enabled="true"/>
                </local-cache>
                <!-- End of local cache called 'library' definition -->

            </cache-container>
            <cache-container name="security"/>
        </subsystem>

Build and Run the Quickstart
----------------------------

_NOTE: The following build command assumes you have configured your Maven user settings. If you have not, you must include Maven setting arguments on the command line. See [the main README](../README.md) for more information._

1. Open a command line and navigate to the root directory of this quickstart.
2. Type this command to build and deploy the archive:

        mvn clean package

3. This will create 2 files: `target/remote-tasks-with-streams-lib.jar` and `target/remote-tasks-with-streams-service.jar`.  
   The first jar only contains the Book.java class which will be added as a dependency for infinispan-commons module (see below for more details) and is necessary for being able to put Book type of objects
    into the cache which compatibility mode is enabled.   
    The second jar contains the remote task services. The jar will be copied to deployments folder (see below) so that when the server is started, the server tasks are deployed for further usage.
4. Run the commands below for proper server configuration:

* Run the following command for copying the `target/remote-tasks-with-streams-service.jar` to be placed under JDG server's `standalone/deployments` directory:

        For Linux:    cp target/remote-tasks-with-streams-service.jar $JDG_HOME/standalone/deployments/
        For Windows:  COPY "target\remote-tasks-with-streams-service.jar" "%JDG_HOME%\standalone\deployments\"

* Run the following command for creating a new module in JDG server and copying the `target/remote-tasks-with-streams-lib.jar` into it.

       For Linux:    mkdir -p $JDG_HOME/modules/system/layers/base/org/jboss/as/quickstarts/remote-task-with-streams/main
       For Windows:  MD %JDG_HOME%\modules\system\layers\base\org\jboss\as\quickstarts\remote-task-with-streams\main

       For Linux:    cp target/remote-tasks-with-streams-lib.jar $JDG_HOME/modules/system/layers/base/org/jboss/as/quickstarts/remote-task-with-streams/main/  
       For Windows:  COPY "target\remote-tasks-with-streams-lib.jar" "%JDG_HOME%\modules\system\layers\base\org\jboss\as\quickstarts\remote-task-with-streams\main\"

* Copy the `resources/module.xml` file to `$JDG_HOME/modules/system/layers/base/org/jboss/as/quickstarts/remote-task-with-streams/main/`:

       For Linux:    cp target/classes/module.xml $JDG_HOME/modules/system/layers/base/org/jboss/as/quickstarts/remote-task-with-streams/main/  
       For Windows:  COPY "target\classes\module.xml" "%JDG_HOME%\modules\system\layers\base\org\jboss\as\quickstarts\remote-task-with-streams\main\"

* Finally add the following line in `$JDG_HOME/modules/system/layers/base/org/infinispan/commons/main/module.xml` between `<dependencies/>` tags:

       <module name="org.jboss.as.quickstarts.remote-task-with-streams" />

After all steps above are done, you can start the JDG server in the following way:

1. Open a command line and navigate to the root of the JDG directory.
2. The following shows the command line to start the server with the web profile:

        For Linux:   $JDG_HOME/bin/standalone.sh
        For Windows: %JDG_HOME%\bin\standalone.bat

3. Run the example application from the quickstart's root directory:

        mvn exec:java

Using the application
---------------------
Basic usage scenarios can look like this (keyboard shortcuts will be shown to you upon start):

        ab  -  add a book
        rb  -  remove a book
        p   -  print all books sorted by title (alphabetically)
        qb  -  query book/s according to inserted parameters
        h   -  shows the all available options
        q   -  quit

Debug the Application
------------------------------------

If you want to debug the source code or look at the Javadocs of any library in the project, run either of the following commands to pull them into your local repository. The IDE should then detect them.

    mvn dependency:sources
    mvn dependency:resolve -Dclassifier=javadoc
