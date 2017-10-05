cdi-jdg: Example Using Cache Injection and JCache annotations
=====================================
Author: Kevin Pollet, Sebastian Laskawiec
Level: Beginner
Technologies: Infinispan, CDI, JCache
Summary: The `cdi-jdg` quickstart demonstrates how to inject Infinispan caches into application and how to use JCache annotations such as @CacheResult.
Target Product: JDG
Product Versions: JDG 7.x, EAP 7.x
Source: <https://github.com/infinispan/jdg-quickstart>

What is it?
-----------

The `cdi-jdg` quickstart demontrates injection of Infinispan caches into a web application using CDI. It it worth to mention
that dependencies used in this quickstart needs to be present in EAP instance. In other words, one needs to install JDG
EAP modules. If one wishes to run this quickstart without EAP modules, the scope of JDG dependencies must be changed
from `provided` into `compile`. A manifest entry with dependencies also needs to be removed.

Additionally, this quickstart uses JCache integration which makes accessing Cache much easier.

System requirements
-------------------

All you need to build this project is Java 8.0 (Java SDK 1.8) or better, Maven 3.0 or better.

The application this project produces is designed to be run on Red Hat JBoss Enterprise Application Platform 7.0 or later.

Configure Maven
---------------

If you have not yet done so, you must [Configure Maven](https://github.com/jboss-developer/jboss-developer-shared-resources/blob/master/guides/CONFIGURE_MAVEN.md#configure-maven-to-build-and-deploy-the-quickstarts) before testing the quickstarts.

Install EAP Modules
--------------------

Download JDG EAP modules and copy them to `%JBOSS_HOME%/modules` directory.

Start EAP
---------

1. Open a command line and navigate to the root of the EAP server directory.
2. The following shows the command line to start the server with the web profile:

        For Linux:   $JBOSS_HOME/bin/standalone.sh
        For Windows: %JBOSS_HOME%\bin\standalone.bat

Build and Deploy the Application in Library Mode
------------------------------------------------

1. Make sure you have started EAP as described above.
2. Open a command line and navigate to the root directory of this quickstart.
3. Type this command to build and deploy the archive:

        mvn clean package wildfly:deploy

4. This will deploy `target/infinispan-cdi.war` to the running instance of the server.


Access the application
---------------------

The application will be running at the following URL: <http://localhost:8080/infinispan-cdi/>

Undeploy the Archive
--------------------

1. Make sure you have started EAP as described above.
2. Open a command line and navigate to the root directory of this quickstart.
3. When you are finished testing, type this command to undeploy the archive:

        mvn wildfly:undeploy

Test the Application using Arquillian and remote EAP instance
-----------------------------------------------------------------

If you would like to test the application, there are a couple of unit tests designed to run on a remote EAP/Wildfly instance.

In order to run those test, please do the following steps:

1. Start EAP/Wildfly
2. Build the quickstart using:

        mvn clean test -Peap-remote
