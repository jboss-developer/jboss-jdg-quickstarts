hotrod-transactions: Example Using Remote Transactions to Cache via HotRod
===========================================================================
Author: Katia Aresti
Level: Intermediate
Technologies: Infinispan, Hot Rod, Transactions
Summary: The `hotrod-transactions` quickstart demonstrates how to use Infinispan remote transactions using Hot Rod
Target Product: JDG
Product Versions: JDG 7.2.x or above
Source: <https://github.com/infinispan/jdg-quickstart>

What is it?
-----------

Hot Rod is a binary TCP client-server protocol used in JBoss Data Grid. The Hot Rod protocol facilitates faster client and server interactions in comparison to other text based protocols and allows clients to make decisions about load balancing, failover and data location operations.

This quickstart demonstrates how to connect remotely to JBoss Data Grid (JDG) to store, retrieve, and remove data from cache using the Hot Rod protocol. 
It is a simple Football Manager console application allows you to add and remove teams, add players to or remove players from teams, or print a list of the current teams, 
players and their countries using the Hot Rod based connector.
All the operations are done in a transactional scope.


System requirements
-------------------

All you need to build this project is Java 8.0 (Java SDK 1.8) or better, Maven 3.0 or better.

The application this project produces is designed to be run on JBoss Data Grid 7.2 or above

 
Configure Maven
---------------

If you have not yet done so, you must [Configure Maven](https://github.com/jboss-developer/jboss-developer-shared-resources/blob/master/guides/CONFIGURE_MAVEN.md#configure-maven-to-build-and-deploy-the-quickstarts) before testing the quickstarts.


Configure JDG
-------------

1. Obtain JDG server distribution on Red Hat's Customer Portal at https://access.redhat.com/jbossnetwork/restricted/listSoftware.html

The caches are configured programatically using the Hot Rod administration capabilities.  

Start JDG
---------

1. Open a command line and navigate to the root of the JDG directory.
2. The following shows the command line to start the server with the web profile:

        For Linux:   $JDG_HOME/bin/standalone.sh
        For Windows: %JDG_HOME%\bin\standalone.bat


Build and Run the Quickstart
----------------------------

_NOTE: The following build command assumes you have configured your Maven user settings. If you have not, you must include Maven setting arguments on the command line. See [Build and Deploy the Quickstarts](../../README.md#build-and-deploy-the-quickstarts) for complete instructions and additional options._

1. Make sure you have started the JDG as described above.
2. Open a command line and navigate to the root directory of this quickstart.
3. Type this command to build and deploy the archive:

        mvn clean package 
                
4. This will create a file at `target/jboss-hotrod-endpoint-quickstart.jar`

5. Run the example application in its directory:

        mvn exec:java
 

Using the application
---------------------
Basic usage scenarios can look like this (keyboard shortcuts will be shown to you upon start):

        at  -  add a team
        ap  -  add a player to a team
        rt  -  remove a team
        rp  -  remove a player from a team
        p   -  print all teams and players
        pc  -  print all players and countries
        q   -  quit
        
Type `q` one more time to exit the application.

Run application with different classpath
----------------------------------------
It's possible to run this quickstart with different classpath (other than default created by mvn exec:java).
To do this, compile quickstart with:

        mvn clean package -Pcustom-classpath -Dclasspath=/custom/classpath/directory

This will create a file at `target/jboss-hotrod-endpoint-quickstart.jar`.
Then you can run it with:

        java -jar target/jboss-hotrod-endpoint-quickstart.jar

Debug the Application
------------------------------------

If you want to debug the source code or look at the Javadocs of any library in the project, run either of the following commands to pull them into your local repository. The IDE should then detect them.

    mvn dependency:sources
    mvn dependency:resolve -Dclassifier=javadoc


