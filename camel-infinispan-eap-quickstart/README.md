Using Camel with Infinispan in Webapps
======================================

This quickstart demonstrates how to use Camel in a web application using Infinispan cache with the __camel-infinispan__ component. Working with Camel is greatly simplified when either the __Spring Framework__ or __Apache Aries Blueprint__ is used in applications but this quickstart doesn't use or require knowledge of either.

* Author: Vijay Chintalapati

Prerequisites
-------------
1. JDK 1.7
2. Maven 3.0
3. JDG 6.4
4. JBoss EAP 6.3

Runtime Behavior
----------------
Once successfully deployed the application will:

1. Periodically monitor a file-system path for any files to be processed
2. Processes text files filled with `Person` objects serialized in JSON format (one entity per every line) when dropped in to the monitored folder path
3. Unmarshal each line into a Person object and load it into Infinispan cache. Person object's Id property will be used as the key for caching
4. Work directly with the Infinispan cache for any business logic/workflow

Setup
-----
1. Ensure that JDG 6.4 and EAP 6.3 Maven repositories are locally installed and correctly referred to in the settings.xml under ~/.m2/. Refer to [official documentation] (https://access.redhat.com/documentation/en-US/Red_Hat_JBoss_Data_Grid/6.4/html-single/Getting_Started_Guide/index.html#chap-Install_and_Use_the_Maven_Repositories) on how to accomplish this
2. To build the project run `mvn clean package -Dpath=<file path>`. `<file path>` here should point to a read/write folder where the files will be watched for. ***Provide full path to the folder***, for example: `mvn clean package -Dpath=/Users/vchintal/data/inbox` 
3. To deploy it on JBoss EAP (preferably running with a variation of standalone.xml file and no port-offset applied), run `mvn jboss-as:deploy` at command line 

Testing
-------
1. Access the web application's context path. If using the localhost and JBoss EAP started with no port-offsets, the application can be accessed [here] (http://127.0.0.1:8080/jboss-camel-infinispan-eap-quickstart/)
2. Create a text file with entries such as `{ "id":"1", "firstName":"Vijay", "lastName":"Chintalapati" }`, one entry per `Person` entity per line and drop it in to the monitored folder 

Criteria for Successful Testing
-------------------------------
The web page at the application's context path should start displaying the loaded entries in the form of a HTML table.
