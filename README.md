JBoss Data Grid (JDG) Quickstarts
===========================

Introduction
-------------

JBoss Data Grid (JDG) is an open source data grid platform that offers multiple clustered modes, but its true value is observed while running in distributed mode where caches cluster together and expose a large memory heap. 

JBoss Data Grid offers two usage modes:

* _Library mode_  - This mode provides all the binaries required to build and deploy a custom runtime environment. The library usage mode allows local access to a single node in a distributed cluster. This usage mode gives the application access to data grid functionality within a virtual machine in the container being used. Supported containers include JBoss AS 7, JBoss Enterprise Application Platform 6 and Tomcat 7. 
* _Client-server mode_ - This mode provides a managed, distributed and clusterable data grid server. Applications can remotely access the data grid server using Hot Rod, memcached or REST client APIs., the Cache is stored in  a managed, distributed and clusterable data grid server.  Applications can remotely access the data grid server using Hot Rod, memcached or REST client APIs. This web application bundles only the HotRod client and communicates with a remote JBoss Data Grid (JDG) server. The JDG server is configured via the `standalone.xml` configuration file.

The quickstarts included in this distribution were written to demonstrate JBoss Data Grid functionality and features. They provide small, specific, working examples that can be used as a reference for your own project. 

Use of EAP_HOME and JBOSS_HOME Variables
----------------------------------------

The quickstart README files use the *replaceable* value `EAP_HOME` to denote the path to the JBoss EAP 6 installation. When you encounter this value in a README file, be sure to replace it with the actual path to your JBoss EAP 6 installation. 

* If you installed JBoss EAP using the ZIP, the install directory is the path you specified when you ran the command.

* If you installed JBoss EAP using the RPM, the install directory is `/var/lib/jbossas/`.

* If you used the installer to install JBoss EAP, the default path for `EAP_HOME` is `${user.home}/EAP-6.3.0`. 

        For Linux: /home/USER_NAME/EAP-6.3.0/
        For Windows: "C:\Users\USER_NAME\EAP-6.3.0\"

* If you used the JBoss Developer Studio installer to install and configure the JBoss EAP Server, the default path for `EAP_HOME` is `${user.home}/jbdevstudio/runtimes/jboss-eap`.

        For Linux: /home/USER_NAME/jbdevstudio/runtimes/jboss-eap/
        For Windows: "C:\Users\USER_NAME\jbdevstudio\runtimes\jboss-eap" or "C:\Documents and Settings\USER_NAME\jbdevstudio\runtimes\jboss-eap\" 

The `JBOSS_HOME` *environment* variable, which is used in scripts, continues to work as it has in the past.


Available Quickstarts 
---------------------

The following is a list of the currently available quickstarts. The table lists each quickstart name, the technologies it demonstrates, gives a brief description of the quickstart, and the level of experience required to set it up. For more detailed information about a quickstart, click on the quickstart name.
This distribution contains the following quickstarts:

| **Quickstart Name** | **Demonstrated Technologies** | **Description** |
|:-----------|:-----------|:-----------|
| [carmart](carmart/README.md) | Infinispan, CDI | Shows how to use Infinispan instead of a relational database. |
| [carmart-tx](carmart-tx/README.md) | Infinispan, CDI, Transactions | Shows how to use Infinispan instead of a relational database with transactions enabled.|
| [helloworld-jdg](helloworld-jdg/README.md) | Infinispan, CDI | Shows how to use Infinispan in clustered mode, with expiration enabled.|
| [hotrod-endpoint](hotrod-endpoint/README.md) | Infinispan, Hot Rod | Demonstrates how to use Infinispan remotely using the Hot Rod protocol. |
| [memcached-endpoint](memcached-endpoint/README.md) | Infinispan, Memcached | Demonstrates how to use Infinispan remotely using the Memcached protocol. |
| [rapid-stock-market](rapid-stock-market/README.md) | Infinispan, Hot Rod, REST | Demonstrates the use of compatibility mode to access data from multiple protocols. |
| [spring](spring/README.md) | Infinispan, Spring | Demonstrates the use of Spring integration modules. |
| [remote-query](remote-query/README.md) | Infinispan, Hot Rod, Remote Query | Demonstrates how to query Infinispan remotely using the Hot Rod protocol. |
| [rest-endpoint](rest-endpoint/README.md) | Infinispan, REST | Demonstrates how to use Infinispan remotely using the REST protocol. |
| [secure-embedded-cache](secure-embedded-cache/README.md) | Infinispan, CDI, REST | Demonstrates how to secure Infinispan in embedded mode. |
| [secure-embedded-cache](cdi/README.md) | Infinispan, CDI | Demonstrates how to use Infinispan CDI and JCache extension. |
| [camel-jbossdatagrid-fuse](camel-jbossdatagrid-fuse/README.md) | Camel, Infinispan, REST | Demonstrates how to use Camel component camel-jbossdatagrid in JBoss Fuse. |

[TOC-quickstart]

Suggested Approach to the Quickstarts
-------------------------------------

We suggest you approach the quickstarts as follows:

* Regardless of your level of expertise, we suggest you start with the **helloworld** quickstart. It is the simplest example and is an easy way to prove your server is configured and started correctly.
* If you are a beginner or new to JBoss, start with the quickstarts labeled **Beginner**, then try those marked as **Intermediate**. When you are comfortable with those, move on to the **Advanced** quickstarts.
* Some quickstarts are based upon other quickstarts but have expanded capabilities and functionality. If a prerequisite quickstart is listed, be sure to deploy and test it before looking at the expanded version.


System Requirements
-------------------

To run these quickstarts with the provided build scripts, you need the following:

1. Java 1.6, to run JBoss Data Grid, JBoss EAP and Maven. You can choose from the following:
    * OpenJDK
    * Oracle Java SE
    * Oracle JRockit

2. Maven 3.0.0 or newer, to build and deploy the examples
    * If you have not yet installed Maven, see the [Maven Getting Started Guide](http://maven.apache.org/guides/getting-started/index.html) for details.
    * If you have installed Maven, you can check the version by typing the following in a command prompt:

            mvn --version 

3. The JBoss Data Grid server distribution ZIP.
    * For information on how to install and run JBoss Data Grid, refer to the product documentation located on the Customer Portal here: <https://access.redhat.com/site/documentation/en-US/JBoss_Data_Grid/>.


4. The JBoss EAP distribution ZIP.
    * For information on how to install and run JBoss, refer to the product documentation located on the Customer Portal here: <https://access.redhat.com/site/documentation/en-US/JBoss_Enterprise_Application_Platform/>.

4. You can also use [JBoss Developer Studio or Eclipse](#use-jboss-developer-studio-or-eclipse-to-run-the-quickstarts) to run the quickstarts. 


Run the Quickstarts
-------------------

The root folder of each individual quickstart contains a README file with specific details on how to build and run the example. In most cases you do the following:

* [Start the JBoss EAP Server](https://github.com/jboss-developer/jboss-developer-shared-resources/blob/master/guides/START_JBOSS_EAP.md#start-the-jboss-eap-server)
* [Build and deploy the quickstarts](#build-and-deploy-the-quickstarts)

Quickstarts demonstrating the remote protocols will also need a running JBoss Data Grid Server.

### Build and Deploy the Quickstarts

See the README file in each individual quickstart folder for specific details and information on how to run and access the example. 

_Note:_ If you do not configure the Maven settings as described here, [Configure Maven](https://github.com/jboss-developer/jboss-developer-shared-resources/blob/master/guides/CONFIGURE_MAVEN.md#configure-maven-to-build-and-deploy-the-quickstarts), you must pass the configuration setting on every Maven command as follows: ` -s QUICKSTART_HOME/settings.xml`


#### Build the Quickstart Archive

In most cases, you can use the following steps to build the application to test for compile errors or to view the contents of the archive. See the specific quickstart README file for complete details.

1. Open a command prompt and navigate to the root directory of the quickstart you want to build.
2. Use this command if you only want to build the archive, but not deploy it:
   * If you have configured the Maven settings :

            mvn clean install
   * If you have NOT configured settings Maven settings:

            mvn clean install -s QUICKSTART_HOME/settings.xml

