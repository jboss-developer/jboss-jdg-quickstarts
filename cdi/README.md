carmart: Infinispan CDI example
=================================
Author: Kevin Pollet, Sebastian Laskawiec
Level: Beginner
Technologies: Infinispan, CDI
Summary: Shows how to use Infinispan CDI extension together with JCache interceptors
Target Product: JDG
Product Versions: EAP 6.x, JDG 6.x
Source: <https://github.com/infinispan/jdg-quickstart>

What is it?
-----------

Infinispan might be integrated with a web application using CDI Extension.

Additionally this Quickstart uses JCache integration which makes accessing Cache much easier.

System requirements
-------------------

All you need to build this project is Java 7.0 (Java SDK 7.6) or better, Maven 3.0 or better.

The application this project produces is designed to be run on Red Hat JBoss Enterprise Application Platform (6.1 or later) or Wildfly.

Configure Maven
---------------

If you have not yet done so, you must [Configure Maven](https://github.com/jboss-developer/jboss-developer-shared-resources/blob/master/guides/CONFIGURE_MAVEN.md#configure-maven-to-build-and-deploy-the-quickstarts) before testing the quickstarts.

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

        mvn clean package jboss-as:deploy

4. This will deploy `target/infinispan-cdi.war` to the running instance of the server.

Access the application
---------------------

The application will be running at the following URL: <http://localhost:8080/infinispan-cdi/>

Undeploy the Archive
--------------------

1. Make sure you have started EAP as described above.
2. Open a command line and navigate to the root directory of this quickstart.
3. When you are finished testing, type this command to undeploy the archive:

        mvn jboss-as:undeploy

Test the Application using Arquillian and remote Wildfly instance
-----------------------------------------------------------------

If you would like to test the application, there are a couple of unit tests designed to run on a remote EAP/Wildfly instance.

In order to run those test, please do the following steps:

1. Start EAP/Wildfly
2. Build the quickstart using:

        mvn clean test -Peap-remote
