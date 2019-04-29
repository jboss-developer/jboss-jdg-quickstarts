hadoop: Example using the JBoss Data Grid Hadoop connector 
=========================================

Authors: Gustavo Fernandes  
Level: Intermediate  
Summary: The `hadoop` quickstart demonstrates how to use Apache Flink to process data from JBoss Data Grid using the JBoss Data Grid Hadoop Connector.  
Technologies: Apache Flink, JBoss Data Grid, Hadoop   
Target Product: JDG  
Product Versions: JDG 7.x  
Source: <https://github.com/infinispan/jdg-quickstart>

What is it?
-----------

The `hadoop` quickstart will populate a cache with football match results for a given championship. 
Each match has a home team, an away team, and the number of goals each scored. 
An Apache Flink job will process that data and generate the championship standings, where each winning 
team gets 3 points, loosing teams get none, and if there's no winner, each team gets 1 point. 
This sample demonstrate access to the Data Grid data using standard Hadoop `InputFormat` and `OutputFormat` interfaces.

System requirements
-------------------

 * JDK 8+
 * Maven 3+
 * JBoss Data Grid 7.3
 * Apache Flink v1.7
 
Configure Maven
---------------
 
If you have not yet done so, you must [Configure Maven](https://github.com/jboss-developer/jboss-developer-shared-resources/blob/master/guides/CONFIGURE_MAVEN.md#configure-maven-to-build-and-deploy-the-quickstarts) before testing the quickstarts.

Start the JDG Server
---------------------

1. Obtain the JDG server distribution on Red Hat's Customer Portal at https://access.redhat.com/jbossnetwork/restricted/listSoftware.html
2. Start JDG
    * Open a command line and navigate to the root of the JDG directory.
    * The following shows the command line to start the server:
    
            For Linux:   $JDG_HOME/bin/standalone.sh -c clustered.xml 
            
            For Windows: %JDG_HOME%\bin\standalone.bat -c clustered.xml 
            
Start Apache Flink
--------------------

1. Download and unpack v1.7 of [Apache Flink with Hadoop](https://www-eu.apache.org/dist/flink/). On Windows, you can use 7-zip to extract the .tgz file.
The extract location will be referred as `FLINK_HOME`.

2. Start Apache Flink
    * Open a command line and navigate to `FLINK_HOME`
    * The following shows the command line to start the server:
    
            For Linux:   bin/start-cluster.sh
            
            For Windows: bin\start-cluster.bat 
            
The Flink dashboard will be visible at <http://localhost:8081>

Build the Quickstart
----------------------------

_NOTE: The following build command assumes you have configured your Maven user settings. If you have not, you must include Maven setting arguments on the command line. See [Build and Deploy the Quickstarts](../../README.md#build-and-deploy-the-quickstarts) for complete instructions and additional options._

1. Make sure you have started the JDG and Flink as described above.
2. Open a command line and navigate to the root directory of this quickstart.
3. Type this command to build and deploy the archive:

               mvn clean package
               
Load Data in the cache
------------------------------

To load the matches contained in the `matches.txt` file:

   * Open a command line and navigate to the `hadoop` quickstart directory.
   * Run the following command to populate the cache:
   
        java -cp target/jboss-hadoop-quickstart-jar-with-dependencies.jar org.infinispan.quickstart.hadoop.DataLoader

Run the analysis job
----------------------------

The `ChampionshipStandings` job will process the matches and calculate the classification table. 

To run the job:  

 * Open a command line and navigate to the root directory of this quickstart.
 * Run the following command to run the job:
 
         For Linux:   
             $FLINK_HOME/bin/flink run target/jboss-hadoop-quickstart-jar-with-dependencies.jar
                  
         For Windows:
             set FLINK_CONF_DIR=%FLINK_HOME%\conf
             %FLINK_HOME%\bin\flink run target\jboss-hadoop-quickstart-jar-with-dependencies.jar


The output should print the table, showing Atletico Madrid on top:

<pre>
*** La Liga Standings ***

Team                Points
----------------------------
Ath Madrid          90
Barcelona           87
Real Madrid         87
Ath Bilbao          70
Sevilla             63
Villarreal          59
Sociedad            59
Valencia            49
Celta               49
Levante             48
Malaga              45
Vallecano           43
Getafe              42
Espanol             42
Granada             41
Elche               40
Almeria             40
Osasuna             39
Valladolid          36
Betis               25
</pre>
