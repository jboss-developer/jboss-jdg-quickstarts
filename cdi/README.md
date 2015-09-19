Infinispan CDI example
=================================
Author: Kevin Pollet, Sebastian Laskawiec

Level: Beginner

Technologies: Infinispan, CDI

Summary: Shows how to use Infinispan CDI extension together with JCache interceptors

Target Product: Infinispan

Product Versions: WildFly 9.0.1.Final, Infinispan 8.0.1.Final

Source: <https://github.com/infinispan/jdg-quickstart>

What is it?
-----------

Infinispan might be integrated with a web application using CDI Extension.

Additionally this Quickstart uses JCache integration which makes accessing Cache much easier.

System requirements
-------------------

All you need to build this project is Java 8.0 (Java SDK 1.8) or better, Maven 3.0 or better.

The application this project produces is designed to be run on Red Hat JBoss Enterprise Application Platform (6.1 or later) or Wildfly.

Configure Maven
---------------

If you have not yet done so, you must [Configure Maven](../../README.md#configure-maven) before testing the quickstarts.

Start EAP
---------

1. Open a command line and navigate to the root of the EAP server directory.
2. The following shows the command line to start the server with the web profile:

        For Linux:   $JBOSS_HOME/bin/standalone.sh
        For Windows: %JBOSS_HOME%\bin\standalone.bat

Build and Deploy the Application in Library Mode
------------------------------------------------

_NOTE: The following build command assumes you have configured your Maven user settings. If you have not, you must include Maven setting arguments on the command line. See [Build and Deploy the Quickstarts](../README.md#build-and-deploy-the-quickstarts) for complete instructions and additional options._

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

Test the Application using Arquillian and remote Wildfly instance
-----------------------------------------------------------------

If you would like to test the application, there are a couple of unit tests designed to run on a remote EAP/Wildfly instance.

In order to run those test, please do the following steps:

1. Start EAP/Wildfly
2. Build the quickstart using:

        mvn clean test -Pwildfly-remote
