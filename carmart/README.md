CarMart: Basic infinispan example
=================================
Author: Tristan Tarrent, Martin Gencur
Level: Intermediate
Technologies: Infinispan, CDI
Summary: Show how to use Infinispan instead of a relational database.

What is it?
-----------

CarMart is a simple web application that uses Infinispan instead of a relational database.
Users can list cars, add new cars or remove them from the CarMart. Information about each car
is stored in a cache. The application also shows cache statistics like stores, hits, retrievals, etc.

The CarMart quickstart can work in two modes: "library" and "client-server". In library mode, 
all libraries (jar files) are bundled with the application and deployed into the server. Caches are
configured programatically and run in the same JVM as the web application. In client-server mode, 
the web application bundles only HotRod client and communicates with a remote JBoss Data Grid (JDG) server. 
The JDG server is configured via standalone.xml configuration file.


System requirements
-------------------

All you need to build this project is Java 6.0 (Java SDK 1.6) or better, Maven 3.0 or better.

The application this project produces is designed to be run on JBoss Enterprise Application Platform 6 or JBoss AS 7. 

 
Configure Maven
---------------

If you have not yet done so, you must [Configure Maven](../README.md#configure-maven-) before testing the quickstarts.


Start JBoss Enterprise Application Platform 6 or JBoss AS 7
-------------------------

1. Open a command line and navigate to the root of the JBoss server directory.
2. The following shows the command line to start the server with the web profile:

        For Linux:   JBOSS_HOME/bin/standalone.sh
        For Windows: JBOSS_HOME\bin\standalone.bat


Build and Deploy the Quickstart
-------------------------

_NOTE: The following build command assumes you have configured your Maven user settings. If you have not, you must include Maven setting arguments on the command line. See [Build and Deploy the Quickstarts](../README.md#buildanddeploy) for complete instructions and additional options._

1. Make sure you have started the JBoss Server as described above.
2. Open a command line and navigate to the root directory of this quickstart.
3. Type this command to build and deploy the archive:

        mvn clean package jboss-as:deploy
        
4. This will deploy `target/carmart-quickstart.war` to the running instance of the server.
 

Access the application
---------------------

        Access the running application in a browser at the following URL:  http://localhost:8080/carmart-quickstart/


        Users can list cars, add new cars or remove them from the CarMart. Information about each car
        is stored in a cache. The application also shows cache statistics like stores, hits, retrievals, etc.


Undeploy the Archive
--------------------

1. Make sure you have started the JBoss Server as described above.
2. Open a command line and navigate to the root directory of this quickstart.
3. When you are finished testing, type this command to undeploy the archive:

        mvn jboss-as:undeploy


Debug the Application
------------------------------------

Contributor: For example: 

If you want to debug the source code or look at the Javadocs of any library in the project, run either of the following commands to pull them into your local repository. The IDE should then detect them.

    mvn dependency:sources
    mvn dependency:resolve -Dclassifier=javadoc




Building and starting the application in client-server mode (using HotRod client)
---------------------------------------------------------------------------------

NOTE: The application must be deployed into JBoss AS7, not JDG, since JDG does not support deploying applications. 

0) Obtain JDG server distribution

1) Add the following configuration to your `$JDG_HOME/standalone/configuration/standalone.xml` to configure
   remote datagrid

    `<paths>
        <path name="temp" path="/tmp"/>
     </paths>`
    
    ...right after `</system-properties>` tag

    `<local-cache name="carcache" start="EAGER" batching="false" indexing="NONE">
        <locking isolation="REPEATABLE_READ" striping="false" acquire-timeout="20000" concurrency-level="500"/>
        <eviction strategy="LIRS" max-entries="4"/>
        <file-store relative-to="temp" path="carstore" passivation="false"/>
     </local-cache>`
    
    ...into infinispan sybsystem
   
2) Start the JDG server (this server is supposed to run on *test1* address)
    
    `$JDG_HOME/bin/standalone.sh`

3) Start JBoss AS 7 into which you want to deploy your application

    `$JBOSS_HOME/bin/standalone.sh`

4) Edit src/main/resources/META-INF/JDG.properties file and specify address of the JDG server

    datagrid.address=test1

5) Build the application in the example's directory:

    `mvn clean package -Premote`

6) Deploy the application

    `mvn jboss-as:deploy -Premote`

7) Go to http://localhost:8080/carmart-quickstart

8) Undeploy the application

    `mvn jboss-as:undeploy -Premote`


