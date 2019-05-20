remote-query: Example Using Remote Query via Hot Rod
==================================
Author: Adrian Nistor  
Level: Intermediate  
Technologies: Infinispan, Hot Rod, Remote Query, Protostream  
Summary: The `remote-query` quickstart demonstrates how to query Infinispan cache remotely using the Hot Rod client.  
Target Product: Data Grid  
Product Versions: Data Grid 7.3.x  
Source: <https://github.com/infinispan/jdg-quickstart>

What is it?
-----------

Hot Rod is a binary TCP client-server protocol used in Red Hat JBoss Data Grid (Data Grid). The Hot Rod protocol facilitates faster client and server interactions in comparison to other text based protocols and allows clients to make decisions about load balancing, failover and data location operations.

The `remote-query` quickstart demonstrates how to connect remotely to Data Grid to store, retrieve, remove and query data from cache using the Hot Rod protocol. It contains two sample applications. One is a simple address book manager console application (AddressBookManager) that allows
you to create, edit and remove Persons, manage a list of phone numbers for each Person, query and print the contents of the data grid, all using the Hot Rod based connector. The second one (SnowForecast) is similar but it focuses on continuous queries and queries with grouping and aggregation.


System requirements
-------------------

All you need to build this project is Java 8.0 (Java SDK 1.8) or newer, Maven 3.0 or newer.

The application this project produces is designed to be run on Red Hat JBoss Data Grid 7.3.x


Configure Maven
---------------

If you have not yet done so, you must [Configure Maven](https://github.com/jboss-developer/jboss-developer-shared-resources/blob/master/guides/CONFIGURE_MAVEN.md#configure-maven-to-build-and-deploy-the-quickstarts) before testing the quickstarts.


Configure Data Grid
-------------------

1. Obtain Data Grid server distribution on Red Hat's Customer Portal at https://access.redhat.com/jbossnetwork/restricted/listSoftware.html

2. Install a JDBC driver into Data Grid. Because Data Grid includes H2 by default, you can skip this step for the scope of this example. Find more information at [Datasource Management in the JBoss Enterprise Application Platform documentation](https://access.redhat.com/documentation/en-us/red_hat_jboss_enterprise_application_platform/7.2/html-single/configuration_guide/index#datasource_management) . _NOTE: Data Grid does not support deploying applications so one cannot install it as a deployment._

3. This Quickstart uses JDBC to store the cache. To permit this, it's necessary to alter Data Grid configuration file (`RHDG_HOME/standalone/configuration/clustered.xml`) to contain the following definitions:

* Datasource subsystem definition:


        <subsystem xmlns="urn:jboss:domain:datasources:4.0">
            <!-- Define this Datasource with jndi name  java:jboss/datasources/ExampleDS -->
            <datasources>
                <datasource jndi-name="java:jboss/datasources/ExampleDS" pool-name="ExampleDS" enabled="true" use-java-context="true">
                    <!-- The connection URL uses H2 Database Engine with in-memory database called test -->
                    <connection-url>jdbc:h2:mem:test;DB_CLOSE_DELAY=-1</connection-url>
                    <!-- JDBC driver name -->
                    <driver>h2</driver>
                    <!-- Credentials -->
                    <security>
                        <user-name>sa</user-name>
                        <password>sa</password>
                    </security>
                </datasource>
                <!-- Define the JDBC driver called 'h2' -->
                <drivers>
                    <driver name="h2" module="com.h2database.h2">
                        <xa-datasource-class>org.h2.jdbcx.JdbcDataSource</xa-datasource-class>
                    </driver>
                </drivers>
            </datasources>
        </subsystem>

* Infinispan subsystem definition:

        <subsystem xmlns="urn:infinispan:server:core:8.5">
            <cache-container name="clustered" default-cache="default" statistics="true">
                <transport lock-timeout="60000"/>
                <global-state/>
                <!-- Add a distributed cache named 'addressbook_indexed' -->
                <distributed-cache name="addressbook_indexed" start="EAGER">
                    <!-- Define the locking isolation of this cache -->
                    <locking acquire-timeout="20000" concurrency-level="500" striping="false"/>

                    <!-- Enable indexing using default configs -->
                    <indexing index="LOCAL" auto-config="true"/>

                    <!-- Define the JdbcBinaryCacheStores to point to the ExampleDS previously defined -->
                    <string-keyed-jdbc-store datasource="java:jboss/datasources/ExampleDS" passivation="false" preload="false" purge="false">
                        <!-- specifies information about database table/column names and data types -->
                        <string-keyed-table prefix="JDG">
                            <id-column name="id" type="VARCHAR"/>
                            <data-column name="datum" type="BINARY"/>
                            <timestamp-column name="version" type="BIGINT"/>
                        </string-keyed-table>
                    </string-keyed-jdbc-store>
                </distributed-cache>

                <!-- Configure index caches -->
                <replicated-cache name="LuceneIndexesLocking">
                    <indexing index="NONE"/>
                </replicated-cache>
                <replicated-cache name="LuceneIndexesMetadata">
                    <indexing index="NONE"/>
                </replicated-cache>
                <distributed-cache name="LuceneIndexesData">
                    <indexing index="NONE"/>
                </distributed-cache>
                <!-- End of 'addressbook_indexed' cache definition -->

                <!-- Add a local cache named 'addressbook' which is not indexed -->
                <distributed-cache name="addressbook" start="EAGER">
                    <!-- Define the locking isolation of this cache -->
                    <locking acquire-timeout="20000" concurrency-level="500" striping="false"/>
                    <!-- Define the JdbcBinaryCacheStores to point to the ExampleDS previously defined -->
                    <string-keyed-jdbc-store datasource="java:jboss/datasources/ExampleDS" passivation="false" preload="false" purge="false">
                        <!-- specifies information about database table/column names and data types -->
                        <string-keyed-table prefix="JDG">
                            <id-column name="id" type="VARCHAR"/>
                            <data-column name="datum" type="BINARY"/>
                            <timestamp-column name="version" type="BIGINT"/>
                        </string-keyed-table>
                     </string-keyed-jdbc-store>
                </distributed-cache>
                <!-- End of 'addressbook' cache definition -->
                <distributed-cache name="default"/>
                <distributed-cache name="memcachedCache"/>
            </cache-container>
        </subsystem>

Start Data Grid NODES
---------------

1. Open a command line and navigate to the root of the Data Grid directory.
2. The following shows the command line to start one of the servers:

        For Linux:   $RHDG_HOME/bin/standalone.sh  -c clustered.xml -Djboss.server.log.dir=standalone/logs/server1 -Djboss.node.name=node1 -Djboss.server.data.dir=standalone/data/node1
        For Windows: %RHDG_HOME%\bin\standalone.bat -c clustered.xml -Djboss.server.log.dir=standalone/logs/server1 -Djboss.node.name=node1 -Djboss.server.data.dir=standalone/data/node1

3. (Optional) In another terminal, start another node by pointing to the same folder and executing:

        For Linux:    $RHDG_HOME/bin/standalone.sh -c clustered.xml -Djboss.server.log.dir=logs/server2 -Djboss.node.name=node2 -Djboss.server.data.dir=data2 -Djboss.socket.binding.port-offset=1000
        For Windowds: %RHDG_HOME%\bin\standalone.bat -c clustered.xml -Djboss.server.log.dir=logs/server2 -Djboss.node.name=node2 -Djboss.server.data.dir=data2 -Djboss.socket.binding.port-offset=1000


Build and Run the Quickstart
----------------------------

_NOTE: The following build command assumes you have configured your Maven user settings. If you have not, you must include Maven setting arguments on the command line. See [the main README](../../README.md) for more information._

1. Make sure you have started the Data Grid as described above.
2. Open a command line and navigate to the root directory of this quickstart.
3. Type this command to build and deploy the archive:

        mvn clean package

4. This will create a file at `target/jboss-remote-query-quickstart.jar`

5. Run the example application in its directory:

        mvn exec:java

This will execute the AddressBookManager application by default. To run the SnowForecast application you will need to activate the SnowForecast profile.

        mvn exec:java -PSnowForecast


Using the application
---------------------
Basic usage scenarios can look like this (keyboard actions will be shown to you upon start):

    Available actions:
     0. Display available actions
     1. Add person
     2. Remove person
     3. Add phone to person
     4. Remove phone from person
     5. Query persons by name
     6. Query persons by phone
     7. Add memo
     8. Query memo by author
     9. Display all cache entries
    10. Run Ickle query
    11. Clear cache
    12. Quit    


Type `12` to exit the application.

Run application with different classpath
----------------------------------------
It's possible to run this quickstart with different classpath (other than default created by mvn exec:java).
To do this, compile quickstart with:

        mvn clean package -Pcustom-classpath,AddressBookManager  -Dclasspath=/custom/classpath/directory

This will create a file at `target/jboss-remote-query-quickstart.jar`.
Then you can run it with:

        java -jar target/jboss-remote-query-quickstart.jar

This will execute the AddressBookManager application by default. To run the SnowForecast application you will need to activate the SnowForecast profile.

        mvn clean package -Pcustom-classpath,SnowForecast  -Dclasspath=/custom/classpath/directory
Then you can run it with:

        java -jar target/jboss-remote-query-quickstart.jar        

Debug the Application
---------------------

If you want to debug the source code or look at the Javadocs of any library in the project, run either of the following commands to pull them into your local repository. The IDE should then detect them.

    mvn dependency:sources
    mvn dependency:resolve -Dclassifier=javadoc


Build and Run the C++ Quickstart
--------------------------------

Install the C++ HotRod client from source or one of the pre-built packages.

Compile and run the quickstart:

    $ cd src/main/cpp
    $ mkdir build_linux
    $ cd build_linux
    $ cmake ..
    $ cmake --build .
    $ ./quickstart [<host>] [<port>]

A menu similar to the Java quickstart will be displayed.
