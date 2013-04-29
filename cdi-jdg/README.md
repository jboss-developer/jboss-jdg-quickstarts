cdi-jdg: Basic JDG example
==========================
Author: Kevin Pollet
Level: Intermediate
Technologies: Infinispan, CDI
Summary: Demonstrates CDI support for JDG
Target Product: JDG
Product Versions: EAP 6.1, EAP 6.2, JDG 6.2
Source: <https://github.com/infinispan/jdg-quickstart>

What is it?
-----------

cdi-jdg is a basic example that shows how to configure and inject the JDG's Cache API. The example also
shows partial support for (JSR-107) caching annotations.

JDG is configured in a local mode. Entries have their lifespan (expiration) and are removed from the cache
after 60 seconds since last update. The eviction is configured and at most 4 entries can be placed into cache before
least recently used entries are evicted.

cdi-jdg example works in _Library mode_. In this mode, the application and the data grid are running in the same
JVM. All libraries (JAR files) are bundled with the application and deployed to Red Hat JBoss Enterprise Application Platform (EAP) 6.1 or later.
The library usage mode only allows local access to a single node in a distributed cluster. This usage
mode gives the application access to data grid functionality within a virtual machine in the container being used.


System requirements
-------------------

All you need to build this project is Java 6.0 (Java SDK 1.6) or better, Maven 3.0 or better.

The application this project produces is designed to be run on Red Hat JBoss Enterprise Application Platform (EAP) 6.1 or later.

 
Configure Maven
---------------

If you have not yet done so, you must [Configure Maven](../../README.md#configure-maven) before testing the quickstarts.


Start EAP
---------

1. Open a command line and navigate to the root of the EAP server directory.
2. The following shows the command line to start the server with the web profile:

        For Linux:   $JBOSS_HOME/bin/standalone.sh
        For Windows: %JBOSS_HOME%\bin\standalone.bat


Build and Deploy the Quickstart
-------------------------------

_NOTE: The following build command assumes you have configured your Maven user settings. If you have not, you must
include Maven setting arguments on the command line. See [Build and Deploy the Quickstarts](../README.md#buildanddeploy)
for complete instructions and additional options._

1. Make sure you have started one instance of the JBoss Server as described above.
2. Open a command line and navigate to the root directory of this quickstart.
3. Type this command to build and deploy the archive to the first server:

        mvn clean package jboss-as:deploy

4. This will deploy `target/jdg-cdi.war` to the server.


Access the application
----------------------

The application will be running at the following URL:

   <http://localhost:8080/cdi-jdg>


Undeploy the Archive
--------------------

1. Make sure you have started the JBoss Server as described above.
2. Open a command line and navigate to the root directory of this quickstart.
3. When you are finished testing, type this command to undeploy the archive from the running server:

        mvn jboss-as:undeploy


Run the Quickstart in JBoss Developer Studio or Eclipse
-------------------------------------------------------
You can also start the server and deploy the quickstarts from Eclipse using JBoss tools. For more information,
see [Use JBoss Developer Studio or Eclipse to Run the Quickstarts](../README.md#useeclipse)


Debug the Application
---------------------

If you want to debug the source code or look at the Javadocs of any library in the project, run either of the following
commands to pull them into your local repository. The IDE should then detect them.

        mvn dependency:sources
        mvn dependency:resolve -Dclassifier=javadoc


