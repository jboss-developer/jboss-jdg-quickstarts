spring-session-jdg: Example Using Spring Session and Infinispan integration
===========================================================================
Author: Sebastian Łaskawiec
Level: Intermediate
Technologies: Infinispan, Spring Boot, Spring Session
Summary: The `spring4-session-jdg` quickstart demonstrates how to use Infinispan in a Spring application and integration with session container.
Target Product: JDG
Product Versions: JDG 7.x
Source: <https://github.com/infinispan/jdg-quickstart>

What is it?
-----------

The `spring4-session-jdg` quickstart shows how to use Infinispan with [Spring Boot](https://projects.spring.io/spring-boot) and 
[Spring Session](http://projects.spring.io/spring-session/).

The project can be ran using standalone mode (Spring Boot can use embedded Tomcat container) or can be deployed
on JWS/Tomcat installation.

Spring Session provides transparent mechanism for storing sessions (e.g. created in `Servlet`s) in custom database. In case
of this demo, this will be an Embedded JBoss Data Grid instance (but you can easily switch to a remote one if you wish). 

System requirements
-------------------

All you need to build this project is Java 8.0 (Java SDK 1.8) or better, Maven 3.0 or better.

JWS/Tomcat installation is optional.

Configure Maven
---------------

If you have not yet done so, you must [Configure Maven](https://github.com/jboss-developer/jboss-developer-shared-resources/blob/master/guides/CONFIGURE_MAVEN.md#configure-maven-to-build-and-deploy-the-quickstarts) before testing the quickstarts.

Build the Quickstart
--------------------

Type this command to build the quickstart:

        mvn clean package

Run the Quickstart in standalone mode (suggested) 
-------------------------------------------------

Run Spring Boot together with embedded Tomcat container by running

        mvn spring-boot:run

Navigate to <http://localhost:8080/sessions> and refresh browser a couple of times. Note the new sessions are
created and stored into the Grid.

Deploy the Quickstart on JWS/Tomcat 
-----------------------------------

Build the application using the following command

        mvn package

Deploy it to JWS/Tomcat using your favorite technique and navigate to <http://localhost:8080/infinispan-spring-session/sessions>.
Now refresh browser a couple of times. Note the new sessions are created and stored into the Grid.
