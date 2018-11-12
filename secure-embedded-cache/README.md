Securing Access to an Embedded Cache
==============================================
Author: Vijay Chintalapati
Level: Intermediate
Technologies: Infinispan, CDI, JAX-RS
Summary: Learn how cache-level authentication and authorization works for Red Hat Data Grid in Library mode.
Target Product: Red Hat Data Grid
Product Versions: Red Hat Data Grid 7.3 or later, Red Hat JBoss Enterprise Application Platform 7.1 or later, Red Hat Single Sign-On Server 7.2 or later
Source: <https://github.com/jboss-developer/jboss-jdg-quickstarts>

About This Quickstart
-----------
The `secure-embedded-cache` quickstart provides a demo application (webapp) that lets you read and write entries in an embedded cache with different user credentials.

System Requirements
-------------------

* Java 8.0 (Java SDK 1.8) or later.
* Maven 3.0 or later. You must also [configure Maven](https://github.com/jboss-developer/jboss-developer-shared-resources/blob/master/guides/CONFIGURE_MAVEN.md#configure-maven-to-build-and-deploy-the-quickstarts).

To run the demo application, you must install:

* Red Hat JBoss Enterprise Application Platform (EAP) 7.1 or later.
* Red Hat Single Sign-On (RH-SSO) Server 7.2.x.
* RH-SSO Client Adapter for JBoss EAP 7.

Download EAP and RH-SSO from the [Red Hat Customer Portal](https://access.redhat.com/downloads).


Deploying the Demo Application
-------------------------------
To deploy the demo application, do the following:

1. Open a command prompt and navigate to the root directory of this repository.
2. Build and package the demo application.

  ```bash
  $ mvn clean package -DskipTests
  ```

3. Set up your EAP server.

  a. Start your local EAP server in Standalone mode.

    - Linux:   `${JBOSS_HOME}/bin/standalone.sh`
    - Windows: `%JBOSS_HOME%\bin\standalone.bat`

  b. Configure the keycloak subsystem in EAP:

  ```bash
  $ mvn wildfly:execute-commands
  ```

  c. Extract the Client Adapter archive into the root directory of your EAP installation and then run the following command:

  ```bash
  $ ${JBOSS_HOME}/jboss-cli.sh --file=adapter-install-offline.cli
  ```
4. Set up your RH-SSO server.

  a. Start the RH-SSO server with a port offset:

  ```bash
  $ ${KEYCLOAK_HOME}/bin/standalone.sh -Djboss.socket.binding.port-offset=100
  ```

  b. Navigate to the RH-SSO Admin console at http://localhost:8180.

  c. Create an _admin_ user account with the following credentials:

    - Username: __johndoe__
    - Password: __password__

  d. Install the realm, clients, roles, and users for the RH-SSO server.

  ```bash
  $ export PATH=${KEYCLOAK_HOME}/bin:$PATH

  $ ./keycloak-setup/install-demo-realm.sh
  ```

  e. Enter the password for __johndoe__ when prompted.

5. Deploy the demo application in your local EAP server.

  ```bash
  $ mvn wildfly:deploy
  ```

You should now be able to access the demo application at the following URL:

http://localhost:8080/jboss-secure-embedded-cache-quickstart/

The demo application lets you access cache content in a web browser. The cache entries are created with the Java API for RESTful Web Services (JAX-RS).

Testing Authentication and Authorization
----------------------------------------
The demo application includes two users, __reader__ and __admin__. To access the embedded cache in the demo application, you must authenticate as either of these users.

To demonstrate authorization, __reader__ has only read permissions to the embedded cache while __admin__ has both read and write permissions.

_Note:_ The following steps are tested with Mozilla Firefox. If you are using a different browser, you might encounter issues logging in and out of the demo application with different users. To resolve issues with login, you can start a new browser session (Internet Explorer 6 or later) or restart your browser between logins.

To test authentication and authorization, do the following:

1. Navigate to the demo application.
2. Log in as __reader__ with this password: **Password_**.

  The demo application presents a form in your browser where you can perform cache operations.

3. Attempt to add a string key/value pair to the cache.

  You can see that the demo application displays messages that indicate that __reader__ is not authorized for write operations.

4. Log out and then refresh your browser to return to the demo application.
5. Log in as __admin__ with this password: **Strong_password**.
6. Add five new string key/value pairs to the cache.

  You can see that the demo application allows __admin__ to perform write operations to the cache.

7. Delete two of the entries that you created in the cache.
8. Log out, refresh your browser, and then log in as __reader__.
9. Verify that you can see the three cache entries you created as __admin__.
10. Attempt to delete cache entries as __reader__.

  Again, you can see that __reader__ is not authorized to delete entries from the cache.

11. Log out from the demo application.

Running Unit Tests
------------------
This quickstart includes prepared unit tests that you can run as follows:

1. Start your local EAP server in Standalone mode.
2. Configure the keycloak subsystem.

  ```bash
  $ mvn wildfly:execute-commands
  ```
3. Stop your local EAP server.
4. Build and package the demo application.

  ```bash
  $ mvn clean package -DskipTests
  ```
5. Start the RH-SSO server with a port offset.

  ```bash
  $ ${KEYCLOAK_HOME}/standalone.sh -Djboss.socket.binding.port-offset=100
  ```

6. Run the unit tests.

  ```bash
  $ mvn test -DeapHome=/path/to/server -Plibrary-tests
  ```
