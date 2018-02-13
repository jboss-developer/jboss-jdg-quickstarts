hotrod-endpoint-js: Javascript example to remotely access Caches via Hot Rod
=========================================
Author: Galder Zamarreño
Level: Intermediate
Technologies: Infinispan, Hot Rod
Summary: The `hotrod-endpoint-js` quickstart demonstrates how to use 
Infinispan cache remotely using the Javascript client for the Hot Rod protocol.
Target Product: JDG
Product Versions: JDG 7.x
Source: <https://github.com/infinispan/jdg-quickstart>

What is it?
-----------

Hot Rod is a binary TCP client-server protocol used in JBoss Data Grid. 
The Hot Rod protocol facilitates faster client and server interactions in 
comparison to other text based protocols and allows clients to make 
decisions about load balancing, failover and data location operations.

This quickstart demonstrates how to connect remotely to JBoss Data Grid (JDG) 
to store, retrieve, and remove data from cache using the Javascript client for 
the Hot Rod protocol. It is a simple Football Manager console application 
allows you to add and remove teams, add players to or remove players from 
teams, or print a list of  the current teams and players using the Hot Rod 
based connector.


System requirements
-------------------

You need Node.js 0.10 or better to build this project. You also need to have NodeJS client installed, either locally with:

        npm install infinispan-<version>.tgz

or globally:

        npm install -g infinispan-<version>.tgz

The application this project produces is designed to be run on JBoss Data Grid 7.x

You also need to install vorpal dependency locally :

        npm install vorpal@1.10.10
 
Configure JDG
-------------

1. Obtain JDG server distribution on Red Hat's Customer Portal at 
https://access.redhat.com/jbossnetwork/restricted/listSoftware.html

2. This Quickstart uses JDBC to store the cache. To permit this, it's 
necessary to alter JDG configuration file (`JDG_HOME/standalone/configuration/standalone.xml`) 
to contain the following definitions:
   
* Infinispan subsystem definition:

        <subsystem xmlns="urn:infinispan:server:core:8.5" default-cache-container="local">
            <cache-container name="local" default-cache="default">
                <local-cache name="default" start="EAGER">
                    <locking acquire-timeout="30000" concurrency-level="1000" striping="false"/>
                </local-cache>
                <local-cache name="memcachedCache" start="EAGER">
                    <locking acquire-timeout="30000" concurrency-level="1000" striping="false"/>
                </local-cache>
                <local-cache name="namedCache" start="EAGER"/>

                <!-- ADD a local cache called 'teams' -->

                <local-cache
                    name="teams"
                    start="EAGER"
                    batching="false">

                    <!-- Define the locking isolation of this cache -->
                    <locking
                        acquire-timeout="20000"
                        concurrency-level="500"
                        striping="false" />

                    <!-- Define the JdbcBinaryCacheStores to point to the ExampleDS previously defined -->
                    <string-keyed-jdbc-store datasource="java:jboss/datasources/ExampleDS" passivation="false" preload="false" purge="false">

                        <!-- specifies information about database table/column names and data types -->
                        <string-keyed-table prefix="JDG">
                            <id-column name="id" type="VARCHAR"/>
                            <data-column name="datum" type="BINARY"/>
                            <timestamp-column name="version" type="BIGINT"/>
                        </string-keyed-table>
                    </string-keyed-jdbc-store>
                </local-cache>
                <!-- End of local cache called 'teams' definition -->

            </cache-container>
            <cache-container name="security"/>
        </subsystem>

Start JDG
---------

1. Open a command line and navigate to the root of the JDG directory.
2. The following shows the command line to start the server:

        For Linux:   $JDG_HOME/bin/standalone.sh
        For Windows: %JDG_HOME%\bin\standalone.bat


Run the Quickstart
------------------

1. Make sure you have started the JDG as described above.
2. Open a command line and navigate to the root directory of this quickstart.
3. Run the example application in its directory:

        node football-manager.js
 

Using the application
---------------------
Basic usage scenarios can look like this (keyboard shortcuts will be shown to you upon start):

    at [teamName]               Add a team.
    ap [teamName] [playerName]  Add a player to a team.
    rt [teamName]               Remove a team.
    rp [teamName] [playerName]  Remove a player from a team.
    p                           Print all teams and players.
    exit                        Exits application.

The application allows team names in `ap`, `rt` and `rp` commands to be 
autocompleted based on the teams that are present in the system.
