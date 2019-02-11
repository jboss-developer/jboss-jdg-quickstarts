spark: Example using the JBoss Data Grid Spark connector to process streaming data
=========================================

Authors: Vojtěch Juránek, Gustavo Fernandes  
Level: Intermediate  
Summary: The `spark` quickstart demonstrates how to process data on the Data Grid from Apache Spark.  
Technologies: Apache Spark, JBoss Data Grid, Scala, Streaming, Listeners, Hot Rod   
Target Product: JDG  
Product Versions: JDG 7.x  
Source: <https://github.com/infinispan/jdg-quickstart>

What is it?
-----------

The `spark` quickstart demonstrates how to use Apache Spark to process data stored in the JBoss Data Grid. The quickstart  
simulates a network of temperature sensors, each sensor will send measurements from different places to the Data Grid.  
The temperatures will be stored on a `Cache<String, Double>`. 
A Spark Streaming job will listen to changes in the temperature readings and will calculate the average for each place, 
storing results back into the "avg-temperatures" cache. Finally, the user can subscribe to one or more cities in order 
to get notified when a temperature average changed.

System requirements
-------------------

 * JDK 8+
 * Maven 3+
 * JBoss Data Grid 7.3.x
 * Apache Spark 2.0.2+
 
Configure Maven
---------------
 
If you have not yet done so, you must [Configure Maven](https://github.com/jboss-developer/jboss-developer-shared-resources/blob/master/guides/CONFIGURE_MAVEN.md#configure-maven-to-build-and-deploy-the-quickstarts) before testing the quickstarts.

Start and configure the JDG Server
------------------------------

1. Obtain the JDG server distribution on Red Hat's Customer Portal at https://access.redhat.com/jbossnetwork/restricted/listSoftware.html
2. Start JDG
    * Open a command line and navigate to the root of the JDG directory.
    * The following shows the command line to start the server:
    
            For Linux:   $JDG_HOME/bin/standalone.sh
            
            For Windows: %JDG_HOME%\bin\standalone.bat
            
3. Create a local cache called `avg-temperatures`:
    * Open a command line and navigate to the root of the JDG directory.
    * The following shows the commands used to create the cache:
    
            For Linux:   
                bin/ispn-cli.sh -c command="/subsystem=datagrid-infinispan/cache-container=local/configurations=CONFIGURATIONS/local-cache-configuration=avg-temperatures:add(start=EAGER,template=false)"
                bin/ispn-cli.sh -c command="/subsystem=datagrid-infinispan/cache-container=local/local-cache=avg-temperatures:add(configuration=avg-temperatures)"
                
            For Windows:
                bin\ispn-cli.bat -c command="/subsystem=datagrid-infinispan/cache-container=local/configurations=CONFIGURATIONS/local-cache-configuration=avg-temperatures:add(start=EAGER,template=false)"
                bin\ispn-cli.bat -c command="/subsystem=datagrid-infinispan/cache-container=local/local-cache=avg-temperatures:add(configuration=avg-temperatures)"


Start and configure Apache Spark
------------------------------

1. Download and unpack version 2.0.2+ of [Apache Spark](http://spark.apache.org/downloads.html), picking as package a pre-built version like ` Pre-built for Hadoop 2.6 and later`.  On Windows, you can use 7-zip to extract the .tgz file.
The extract location will be referred as `SPARK_HOME`

_NOTE For Windows users: Spark on windows relies on some binaries not packaged with the .tgz distributed. You'll need to download a Hadoop 2.6 distribution that contains windows binaries on the `\bin` folder such as winutils.exe, and also have a environment variable `HADOOP_HOME` pointing to the hadoop distribution folder._


2. Start Spark master:
    * Open a command line and navigate to `SPARK_HOME`:
    * Run the following command to start the Spark master:
    
                 For Linux:   
                    sbin/start-master.sh --webui-port 9080 -h localhost
                 
                 For Windows: 
                    bin\spark-class.cmd org.apache.spark.deploy.master.Master --webui-port 9080 -h localhost

3. Start the Spark Worker:

  * Open a command line and navigate to `SPARK_HOME`:
  * Run the following command to start a Spark slave:
    
                 For Linux:   
                    sbin/start-slave.sh spark://127.0.0.1:7077 --webui-port 9081  
                 
                 For Windows: 
                    bin\spark-class.cmd org.apache.spark.deploy.worker.Worker spark://127.0.0.1:7077 --webui-port 9081

From now the Spark admin console can be accessed at http://localhost:9080

Build the Quickstart
----------------------------

_NOTE: The following build command assumes you have configured your Maven user settings. If you have not, you must include Maven setting arguments on the command line. See [Build and Deploy the Quickstarts](../../README.md#build-and-deploy-the-quickstarts) for complete instructions and additional options._

1. Make sure you have started the JDG and Spark as described above.
2. Open a command line and navigate to the root directory of this quickstart.
3. Type this command to build and deploy the archive:

               mvn clean package
               
Start the temperature sensor simulation
----------------------------

The directory `temperature-sensor` contains an application to simulate a network of temperature sensors.
The simulation randomly selects one of the European capitals and randomly generates a temperature measurement. 
After that it stores the `(place, temperature)` pairs into Infinispan. It generates such pairs every 100 ms. 

To start the sensor network simulation:

   * Open a command line and navigate to the directory `temperature-sensor` of the spark quickstart:
   * Run the following command to start it:
   
         java -jar target/temperature-sensor-jar-with-dependencies.jar

Submit the Spark Job
----------------------------

The Spark job `TemperatureAnalysis` recomputes average temperatures for each `(place, temperature)` that arrives from the sensor network.

To start the job: 

 * Open a command line and navigate to the directory `spark-temperature-analysis` of the quickstart.
 * Run the following command to start the job:
 
         For Linux:   
             $SPARK_HOME/bin/spark-submit --master spark://127.0.0.1:7077 --class org.infinispan.quickstart.spark.TemperatureAnalysis target/spark-temperature-analysis-jar-with-dependencies.jar
                  
         For Windows:
             %SPARK_HOME%\bin\spark-submit.cmd --master spark://127.0.0.1:7077 --class org.infinispan.quickstart.spark.TemperatureAnalysis target\spark-temperature-analysis-jar-with-dependencies.jar


Start the client application
----------------------------

The directory `temperature-client` is the end user application that is notified about average temperatures changes in selected places.

To start the client application: 

 * Open a command line and navigate to the directory `temperature-client` of the quickstart:
 * Run the following command to start it:
   
         java -jar target/temperature-client-jar-with-dependencies.jar Prague Vienna

The last argument is a space separated list of capitals to subscribe for updates, at least one place is required.
The application will listen for updates for 5 minutes.  


