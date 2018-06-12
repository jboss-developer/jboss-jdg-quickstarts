secure-embedded-cache: Example Using Secured Access to Embedded Cache
==============================================
Author: Vijay Chintalapati
Level: Intermediate
Technologies: Infinispan, CDI, JAX-RS
Summary: The `secure-embedded-cache` quickstart demonstrates how cache level authentication and authorization works in an embedded mode of JDG.
Target Product: JDG
Product Versions: JDG 7.x, EAP 7.1+
Source: <https://github.com/jboss-developer/jboss-jdg-quickstarts>

What is it?
-----------

The `secure-embedded-cache` quickstart demonstrates how to configure security, authentication and authorization, on embedded Infinispan caches. Users
can see the cache content in a web browser. The content is produced using JAX-RS.

System requirements
-------------------

All you need to build this project is Java 8.0 (Java SDK 1.8) or better, Maven 3.1.1 or better.

The application this project produces is designed to be run on Red Hat JBoss Enterprise Application Platform (EAP) 7.1 or later.
This application requires Red Hat Single Sign-On server 7.2.x for user authentication/authorization.


Configure Maven
---------------

If you have not yet done so, you must [Configure Maven](https://github.com/jboss-developer/jboss-developer-shared-resources/blob/master/guides/CONFIGURE_MAVEN.md#configure-maven-to-build-and-deploy-the-quickstarts) before testing the quickstarts.

Setup
-----
1. To build and package the webapp, run the command at the command prompt in the root directory of the project:

    mvn clean package -DskipTests

2. To run the install and configuration commands, ensure that the local EAP server is running in Standalone mode

3. To configure the keycloak subsystem in EAP, run this command:

    mvn wildfly:execute-commands

4. Download Red Hat Single Sign-On (RH-SSO) server and Client Adapter for JBoss EAP 7 from Red Hat's Customer portal.

5. Install the Client Adapter in EAP by unpacking the Client Adapter zip file into the root directory of EAP
   and running the following command:

   ${JBOSS_HOME}/jboss-cli.sh --file=adapter-install-offline.cli

6. Start RH-SSO/Keycloak server with port offset:

    ${KEYCLOAK_HOME}/bin/standalone.sh -Djboss.socket.binding.port-offset=100

7. Open the Keycloak server Admin console running at http://localhost:8180 and create the admin user with the following credentials:

    username: johndoe
    password: password

8. To run the installation script for Keycloak server that will install the necessary realm, clients, roles and users:

    export PATH=${KEYCLOAK_HOME}/bin:$PATH

    ./keycloak-setup/install-demo-realm.sh

   Note: You'll be prompted for a password. Enter johndoe's password.

9. To deploy the packaged webapp in EAP, run the command

    mvn wildfly:deploy

10. Considering a very basic setup of the server, the application should now be accessible at the URL: http://localhost:8080/jboss-secure-embedded-cache-quickstart/


Testing
-------
1. Log in as __reader__ when prompted for a login (password: Password_)
2. Once successfully authenticated, using the form on the page presented to you, try adding a string Key/Value pair. Make a note of any messages displayed.
3. Now, log out and go to the application URL again.
4. You will be prompted with a login again.
5. Now try logging in as __admin__ (password: Strong_password) by clicking on the original URL
6. Repeat testing step #2. If you see the writes being permitted, go ahead and add 5 new entries and delete 2 of them as part of testing
7. Now logout as __admin__ in the same manner as described above
8. Log back in as __reader__ and verify that you see 3 entries in the cache. If Yes, the testing was a SUCCESS. While still logged in as __reader__, see if you could delete any entries from the cache and note any messages displayed

Unit tests
----------
There are prepared unit tests for this quickstart. To run them :

1. Ensure that the local EAP server is running in Standalone mode :

       For Linux:   $JBOSS_HOME/bin/standalone.sh
       For Windows: %JBOSS_HOME%\bin\standalone.bat
2. Configure the keycloak subsystem: `mvn wildfly:execute-commands`
3. Shutdown the EAP server
4. Build and package the webapp with `mvn clean package -DskipTests`
5. Ensure the Keycloak server is configured properly and running with a port offset

    ${KEYCLOAK_HOME}/standalone.sh -Djboss.socket.binding.port-offset=100

6. Run the tests with `mvn test -DeapHome=/path/to/server -Plibrary-tests`

References
----------
<b><sup>1</sup></b> The above shown log out steps work well with Firefox. If it doesn't work well in your environment, you have three choices to log in as a different user:

1. Start a new browser session  (applies to IE 6+)
2. Use another browser 
3. Kill/Start (restart) the browser 
