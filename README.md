QUICKSTART_NAME: Brief Description of the Quickstart
======================================================
Author: Jason Porter
Level: Intermediate
Technologies: CDI
Summary: Creating a basic CDI extension to provide injection of fields from an XML file.

_This file is meant to serve as a template or guideline for your own quickstart README.md file. Be sure to replace QUICKSTART_NAME and YOUR_NAME, with the appropriate values._

Contributor instructions are prefixed with 'Contributor: '

What is it?
-----------

Contributor: This is where you provide an overview of what the quickstart demonstrates. For example:

 * What are the technologies demonstrated by the quickstart?
 * What does it do when you run it?

You should include any information that would help the user understand the quickstart.  

If possible, give an overview, including any code they should look at to understand how it works..


System requirements
-------------------

Contributor: For example: 

All you need to build this project is Java 6.0 (Java SDK 1.6) or better, Maven 3.0 or better.

The application this project produces is designed to be run on JBoss Enterprise Application Platform 6 or JBoss AS 7. 

 
Configure Maven
---------------

Contributor: You can copy or link to the Maven configuration information in the README file in the root folder of the quickstarts. For example:

If you have not yet done so, you must [Configure Maven](../README.md#mavenconfiguration) before testing the quickstarts.


Configure Optional Components
-------------------------

Contributor: If your quickstart requires any additional components, decribe how to set them up here. If your quickstart requires a secured user, PostgreSQL, or Byteman, you can link to the instructions in the README file located in the root folder of the quickstart directory. Here are some examples:

 * This quickstart uses a secured management interface and requires that you create a management (or application) user to access the running application. Instructions to set up a Management (or Application) user can be found here: 

    * [Add a Management User](../README.md#addmanagementuser)

    * [Add an Application User](../README.md#addapplicationuser)

 * This quickstart requires the PostgreSQL database. Instructions to install an configure PostgreSQL can be found here: [Install and Configure the PostgreSQL Database](../README.md#postgresql)

 * This quickstart uses Byteman to help demonstrate crash recovery. Instructions to install and configure Byteman can be found here: [Install and Configure Byteman](../README.md#byteman)


Start JBoss Enterprise Application Platform 6 or JBoss AS 7
-------------------------

Contributor: Does this quickstart require one or more running servers? If so, you must show how to start the server. If you start the server in one of the following 3 ways, you can simply copy the instructions in the README file located in the root folder of the quickstart directory:

 * Start JBoss Enterprise Application Platform 6 or JBoss AS 7 with the Web Profile

 * Start JBoss Enterprise Application Platform 6 or JBoss AS 7 with the Full Profile

 * Start JBoss Enterprise Application Platform 6 or JBoss AS 7 with Custom Options. You will need to provide the argument string to pass on the command line, for example: 

      `--server-config=../../docs/examples/configs/standalone-xts.xml`

Contributor: If the server is started in a different manner than above, give the specific instructions.


Build and Deploy the Quickstart
-------------------------

Contributor: If the quickstart is built and deployed using the standard Maven commands, "mvn clean package" and "mvn jboss-as:deploy", copy the following:

_NOTE: The following build command assumes you have configured your Maven user settings. If you have not, you must include Maven setting arguments on the command line. See [Build and Deploy the Quickstarts](../README.md#buildanddeploy) for complete instructions and additional options._

1. Make sure you have started the JBoss Server as described above.
2. Open a command line and navigate to the root directory of this quickstart.
3. Type this command to build and deploy the archive:

        mvn clean package jboss-as:deploy
4. This will deploy `target/jboss-as-QUICKSTART_NAME.war` (or `target/jboss-as-QUICKSTART_NAME.ear`) to the running instance of the server.
 
Contributor: Be sure to replace the QUICKSTART_NAME. If this quickstart requires different or additional instructions, be sure to modify or add those instructions here.


Access the application (For quickstarts that have a UI component)
---------------------

Contributor: Provide the URL to access the running application. Be sure to make the URL a hyperlink as below, substituting the your quickstart name for the QUICKSTART_NAME. 

        Access the running application in a browser at the following URL:  <http://localhost:8080/jboss-as-QUICKSTART_NAME>


Contributor: Briefly describe what you will see when you access the application. For example: 

        You will be presented with a simple form for adding key/value pairs and a checkbox to indicate whether the updates should be executed using an unmanaged component. 

            If the box is checked, the updates will be executed within a session bean method. 
            If the box is not checked, the transactions and JPA updates will run in a servlet instead of session beans. 

        To list all existing key/value pairs, leave the key input box empty. 
    
        To add or update the value of a key, enter a key and value input boxe and click the submit button to see the results.

Contributor: Add any information that will help them run and understand your quickstart.


Undeploy the Archive
--------------------

Contributor: For example: 

1. Make sure you have started the JBoss Server as described above.
2. Open a command line and navigate to the root directory of this quickstart.
3. When you are finished testing, type this command to undeploy the archive:

        mvn jboss-as:undeploy


Run the Arquillian Tests (For quickstarts that contain Arquillian tests)
-------------------------

Contributor: For example: 

This quickstart provides Arquillian tests. By default, these tests are configured to be skipped as Arquillian tests require the use of a container. 

_NOTE: The following commands assume you have configured your Maven user settings. If you have not, you must include Maven setting arguments on the command line. See [Run the Arquillian Tests](../README.md#arquilliantests) for complete instructions and additional options._

1. Make sure you have started the JBoss Server as described above.
2. Open a command line and navigate to the root directory of this quickstart.
3. Type the following command to run the test goal with the following profile activated:

        mvn clean test -Parq-jbossas-remote 

Contributor: The quickstart README should show what to expect from the the tests

* Copy and paste output from the JUnit tests to show what to expect in the console from the tests.

* Copy and paste log messages output by the application to show what to expect in the server log when running the tests.



Run the Quickstart in JBoss Developer Studio or Eclipse
-------------------------------------
Contributor: For example: 

You can also start the server and deploy the quickstarts from Eclipse using JBoss tools. For more information, see [Use JBoss Developer Studio or Eclipse to Run the Quickstarts](../README.md#useeclipse) 

Debug the Application
------------------------------------

Contributor: For example: 

If you want to debug the source code or look at the Javadocs of any library in the project, run either of the following commands to pull them into your local repository. The IDE should then detect them.

    mvn dependency:sources
    mvn dependency:resolve -Dclassifier=javadoc



Build and Deploy the Quickstart - to OpenShift
-------------------------

Contributor: If the quickstart deploys to OpenShift, you can use the following template a starting point to describe the process. Be sure to note:

* APPLICATION_NAME should be replaced with a variation of the quickstart name, for example: myquickstart
* QUICKSTART_NAME should be replaced with your quickstart name, for example:  my-quickstart

### Create an OpenShift Account and Domain

If you do not yet have an OpenShift account and domain, [Sign in to OpenShift](https://openshift.redhat.com/app/login) to create the account and domain. [Get Started with OpenShift](https://openshift.redhat.com/app/getting_started) will show you how to install the OpenShift Express command line interface.

### Create the OpenShift Application

Open a shell command prompt and change to a directory of your choice. Enter the following command, replacing APPLICATION_TYPE with `jbosseap-6.0` for quickstarts running on JBoss Enterprise Application Platform 6, or `jbossas-7` for quickstarts running on JBoss AS 7:

    rhc app create -a APPLICATION_NAME -t APPLICATION_TYPE

_NOTE_: The domain name for this application will be APPLICATION_NAME-YOUR_DOMAIN_NAME.rhcloud.com`. Here we use the _quickstart_ domain. You will need to replace it with your own OpenShift domain name.

This command creates an OpenShift application named  and will run the application inside the `jbosseap-6.0`  or `jbossas-7` container. You should see some output similar to the following:

    Creating application: APPLICATION_NAME
    Now your new domain name is being propagated worldwide (this might take a minute)...
    Warning: Permanently added 'APPLICATION_NAME-quickstart.rhcloud.com,107.22.36.32' (RSA) to the list of known hosts.
    Confirming application 'APPLICATION_NAME' is available:  Success!
    
    APPLICATION_NAME published:  http://APPLICATION_NAME-quickstart.rhcloud.com/
    git url:  ssh://b92047bdc05e46c980cc3501c3577c1e@APPLICATION_NAME-quickstart.rhcloud.com/~/git/APPLICATION_NAME.git/
    Successfully created application: APPLICATION_NAME

The create command creates a git repository in the current directory with the same name as the application. Notice that the output also reports the URL at which the application can be accessed. Make sure it is available by typing the published url <http://APPLICATION_NAME-quickstart.rhcloud.com/> into a browser or use command line tools such as curl or wget.

### Migrate the Quickstart Source

Now that you have confirmed it is working you can migrate the quickstart source. You do not need the generated default application, so navigate to the new git repository directory and tell git to remove the source and pom files:

    cd APPLICATION_NAME
    git rm -r src pom.xml

Copy the source for the QUICKSTART_NAME quickstart into this new git repository:

    cp -r QUICKSTART_HOME/QUICKSTART_NAME/src .
    cp QUICKSTART_HOME/QUICKSTART_NAME/pom.xml .

### Configure the OpenShift Server

Contributor: Here you describe any modifications needed for the `.openshift/config/standalone.xml` file. See other quickstart README.md files for examples.

### Deploy the OpenShift Application

You can now deploy the changes to your OpenShift application using git as follows:

    git add src pom.xml
    git commit -m "QUICKSTART_NAME quickstart on OpenShift"
    git push

The final push command triggers the OpenShift infrastructure to build and deploy the changes. 

Note that the `openshift` profile in `pom.xml` is activated by OpenShift, and causes the war build by openshift to be copied to the `deployments` directory, and deployed without a context path.

### Test the OpenShift Application

When the push command returns you can test the application by getting the following URL either via a browser or using tools such as curl or wget:

* <http://APPLICATION_NAME-quickstart.rhcloud.com/> 

You can use the OpenShift command line tools or the OpenShift web console to discover and control the application.

### Destroy the OpenShift Application

When you are finished with the application you can destroy it as follows:

        rhc app destroy -a APPLICATION_NAME

_Note_: There is a limit to the number of applications you can deploy concurrently to OpenShift. If the `rhc app create` command returns an error indicating you have reached that limit, you must destroy an existing application before you continue. 

* To view the list of your OpenShift applications, type: `rhc domain show`
* To destroy an existing application, type the following, substituting the application name you want to destroy: `rhc app destroy -a APPLICATION_NAME_TO_DESTROY`

-------------------------------------------------------------------


JDG Quickstarts 
===============

Quickstarts (or examples, or samples) for JBoss Data Grid. There are a number of rules for quickstarts:

* Each quickstart should have a unique name, this enables a user to quickly identify each quickstart
* A quickstart should have a simple build that the user can quickly understand. If using maven it should:
  1. Not inherit from another POM
* The quickstart should be importable into JBoss Tools and deployable there
* The quickstart should be explained in detail in the associated user guide, including how to deploy

If you add a quickstart, don't forget to update `dist/src/main/assembly/README.md`.

The 'dist' folder contains Maven scripts to build a zip of the quickstarts.

How to run endpoint examples ?
------------------------------

Endpoint examples show HotRod, REST and MemCached endpoints connecting to JBoss Data Grid (JDG)
remotely and storing, retrieving and removing data from caches. All of the apps are variations of a simple 
Football Manager which is a console application. It is possible to add a team, players, remove 
all the entities and show the listing of all teams/players. Nothing complex, just showing basic 
operations with the cache.

Building and starting the application
-------------------------------------

0) Install a JDBC driver into JDG. More information can be found at 
   https://community.jboss.org/wiki/DataSourceConfigurationInAS7, topic "Installing a JDBC driver as a module"

NOTE: JDG does not support deploying applications so one cannot install it as a deployment.

1) Alter JDG configuration file (`${JDG_HOME}/standalone/configuration/standalone.xml`) to contain
   the following definitions:
   
* Datasource subsystem definition:

    `<subsystem xmlns="urn:jboss:domain:datasources:1.0">
        <datasources>
            <datasource jndi-name="java:jboss/datasources/ExampleDS" pool-name="ExampleDS" enabled="true" use-java-context="true">
                <connection-url>jdbc:h2:mem:test;DB_CLOSE_DELAY=-1</connection-url>
                <driver>h2</driver>
                <security>
                    <user-name>sa</user-name>
                    <password>sa</password>
                </security>
            </datasource>
            <drivers>
                <driver name="h2" module="com.h2database.h2">
                    <xa-datasource-class>org.h2.jdbcx.JdbcDataSource</xa-datasource-class>
                </driver>
            </drivers>
        </datasources>
    </subsystem>`

* Infinispan subsystem definition:

    `<subsystem xmlns="urn:jboss:domain:infinispan:1.2" default-cache-container="default">
        <cache-container name="default" default-cache="memcachedCache" listener-executor="infinispan-listener" start="EAGER">
            <local-cache 
                name="memcachedCache"
                start="EAGER"
                batching="false"
                indexing="NONE">
                <locking
                    isolation="REPEATABLE_READ"
                    acquire-timeout="20000"
                    concurrency-level="500"
                    striping="false" />
                <transaction mode="NONE" />
                <string-keyed-jdbc-store datasource="java:jboss/datasources/ExampleDS" passivation="false" preload="false" purge="false">
                    <property name="databaseType">H2</property>
                    <string-keyed-table prefix="JDG">
                        <id-column name="id" type="VARCHAR"/>
                        <data-column name="datum" type="BINARY"/>
                        <timestamp-column name="version" type="BIGINT"/>
                    </string-keyed-table>
                </string-keyed-jdbc-store>
            </local-cache>
            <local-cache 
                name="teams"
                start="EAGER"
                batching="false"
                indexing="NONE">
                <locking
                    isolation="REPEATABLE_READ"
                    acquire-timeout="20000"
                    concurrency-level="500"
                    striping="false" />
                <transaction mode="NONE" />
                <string-keyed-jdbc-store datasource="java:jboss/datasources/ExampleDS" passivation="false" preload="false" purge="false">
                    <property name="databaseType">H2</property>
                    <string-keyed-table prefix="JDG">
                        <id-column name="id" type="VARCHAR"/>
                        <data-column name="datum" type="BINARY"/>
                        <timestamp-column name="version" type="BIGINT"/>
                    </string-keyed-table>
                </string-keyed-jdbc-store>
            </local-cache>
        </cache-container>
    </subsystem>`


NOTE: The cache called "teams" will be used by HotRod and REST endpoints; Memcached endpoint works with "memcachedCache" by
      default

2) Each of the submodules (hotrod-endpoint, rest-endpoint, memcached-endpoint) contains a configuration file 
   (`src/main/resources/jdg.properties`). Modify it to point to your JDG installation (default values should be fine
   for most cases)
  
   In order to run hotrod-endpoint example, you also need to go through the following steps: 
   
   * Obtain JDG distribution with productized Infinispan libraries (library distribution)

   * Install libraries needed by HotRod client from the bundle into your local maven repository:

        `mvn initialize -Pinit-repo -Ddatagrid.dist=/home/anyuser/jboss-datagrid-library-6.0.0.ER6-redhat-1`

3) Build the example application in its directory:

    `mvn package`

NOTE: This uses Maven's shade plugin which will bundle all dependencies into one jar file and so running the application is easier.

4) Start your JDG server instance:

    `${JDG_HOME}/bin/standalone.sh`

5) Run the example application in its directory:

* for hotrod-endpoint: 

    `java -jar target/hotrod-endpoint-quickstart.jar`

* for rest-endpoint:

    `java -jar target/rest-endpoint-quickstart.jar`

* for memcached-endpoint:

    `java -jar target/memcached-endpoint-quickstart.jar`


Using the application
---------------------
Basic usage scenarios can look like this (keyboard shortcuts will be shown to you upon start):

1. add a team
2. add players to the team
3. print all teams and their players
4. remove a player from certain team
5. print again
6. remove a team
7. print again

