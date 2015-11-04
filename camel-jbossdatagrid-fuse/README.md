Using __camel_jbossdatagrid__ component
===========================================================
This quickstart shows how to use the __camel_jbossdatagrid__ component in Fuse 6.2.0
* Author: Vijay Chintalapati

This quickstart will deploy two bundles __local_cache_producer__ and __local_cache_consumer__ on Fuse, one on each container __child1__ and __child2__ respectivity.

* __local_cache_producer__ : Scans a folder (/tmp/incoming) for incoming CSV files of the format "id, firstName, lastName, age". If a file is dropped with entries in the given format, each entry is read and transformed into a Person POJO and stored in the data grid

* __local_cache_consumer__ : Lets you query for a POJO using a RESTful interface and get back  a JSON representation of the Person POJO stored in the data grid for the given key
 
_The bundles reside in two different containers and the reason why the consumer is able to extract what the producer has put in, is because of the use of same configuration in files: infinispan.xml and jgroups.xml on both sides. The infinispan.xml file has a **REPL (replicated)** cache definition by the name **camel-cache** and this is the cache with which the producer and consume interact_

System requirements
-------------------

All you need to build this project is Java SDK 1.7 or better, Maven 3.0 or better.

The application this project produces is designed to be run on JBoss Fuse 6.2.0 or later.
 
Configure Maven
---------------

If you have not yet done so, you must [Configure Maven](https://github.com/jboss-developer/jboss-developer-shared-resources/blob/master/guides/CONFIGURE_MAVEN.md#configure-maven-to-build-and-deploy-the-quickstarts) before testing the quickstarts.

Setup the quickstart
----------------------

1. Download the Fuse binary __jboss-fuse-full-6.2.0.redhat-133.zip__ from either http://www.jboss.org/products or from Red Hat's Customer portal
2. Export the path to the folder where the CSV files will be placed by running the command of the form `export incomingFolderPath=[Full path to the CSV folder]`
3. Run `mvn clean install -DincomingFolderPath=$incomingFolderPath` in the root folder of the quickstart
4. Set the following two environment variables in the same shell: 
  1. export __FUSE_INSTALL_PATH__ = _[Full path to the folder into where Fuse will be installed]_ 
  2. export __FUSE_BINARY_PATH__ = _[Full path to the Fuse binary file]_ 
5. While in the root folder of the quickstart, run `./setupEverythingOnFuse.sh`

Setup Verification 
------------------

1. Verify your access to the [Fuse Hawtio Console] (http://127.0.0.1:8181/hawtio/index.html#/login). The __username/password__ is __admin/admin__.
2. If the __Fuse Fabric__ is created right, you should be able to access the containers [here](http://127.0.0.1:8181/hawtio/index.html#/fabric/containers)
3. Verify if you see two containers created by the names __child1__ and __child2__. Also note if root/child1/child2 are all in __green__, if they are not click on their names andcapture the logs displayed.  

Testing
-------

* Testing the __local_cache_producer__: Run a command `echo "1,Bill,Gates,59" > $incomingFolderPath/sample.csv`. If the file disappears in a second or two then the producer worked correctly and you can proceed to testing the consumer
* Testing the __local_cache_consumer__: Open up a browser and hit the url http://127.0.0.1:8282/cache/get/1 (where 1 is the Id with which the Person instance was stored in the grid). If you get a JSON represetation back of the corresponding POJO, the testing was successful
