rapid-stock-market: Example of Compatibility Mode Between HotRod and REST 
===============================================
Author: Martin Gencur
Level: Intermediate
Technologies: Infinispan, HotRod, REST
Summary: The `rapid-stock-market` quickstart demonstrates how to enable Infinispan compatibility mode so that data can be read/written over different protocols.
Target Product: JDG
Product Versions: JDG 7.x
Source: <https://github.com/infinispan/jdg-quickstart>

What is it?
-----------

The `rapid-stock-market` quickstart demonstrates the behaviour of a compatibility mode. Updates of shares are
being stored in a cache via HotRod client and retrieved via REST client.

The quickstart consists of two applications: a server-side application and client-side application.

System requirements
-------------------

All you need to build this project is Java 8.0 (Java SDK 1.8) or better, Maven 3.0 or better.

The application this project produces is designed to be run on Red Hat JBoss Data Grid 7.x

Configure Maven
---------------

If you have not yet done so, you must [Configure Maven](https://github.com/jboss-developer/jboss-developer-shared-resources/blob/master/guides/CONFIGURE_MAVEN.md#configure-maven-to-build-and-deploy-the-quickstarts) before testing the quickstarts.

Configure and Start JDG Server
------------------------------

1) Build a server module for Red Hat JBoss Data Grid (JDG) Server. In this step, a class common to both clients
   and the server is packaged in a single jar file and placed in a directory structure similar to a server module.

    mvn clean package -Pprepare-server-module

2) Install the server module into the server.

   * Copy the prepared module into the server:

        cp -r target/modules ${JDG_HOME}/

   * Add the new module as a dependency of the `org.infinispan.commons` module. I.e. add
     `<module name="org.infinispan.quickstart.compatibility.common"/>` to `module.xml` in
     `modules/system/layers/base/org/infinispan/commons/main`

3) Configure JDG server to use a proper configuration file.

   * Copy an example configuration file for a compatibility mode to a correct location where JDG server
     can pick it up.

        cp ${JDG_HOME}/docs/examples/configs/standalone-compatibility-mode.xml ${JDG_HOME}/standalone/configuration

   * Disable REST security by removing `security-domain` and `auth-method` attribute definitions
     from `<rest-connector>` configuration element.

4) Start JDG server

    ${JDG_HOME}/bin/standalone.sh -c standalone-compatibility-mode.xml

Build and Run the Quickstart
-------------------------

1) Build the application.

    mvn clean package

2) Open another terminal and start the server-side application - Market Updater

    mvn exec:java

3) Open yet another terminal and start the client-side application

    mvn exec:java -Pclient

4) Follow the help for the client application and try it out!

