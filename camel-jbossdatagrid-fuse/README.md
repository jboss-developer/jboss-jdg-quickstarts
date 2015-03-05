Using for __camel_jbossdatagrid__ component
===========================================================
This quickstart shows how to use the __camel_jbossdatagrid__ component in Fuse 6.1.0
* Author: Vijay Chintalapati

This quickstart will deploy two bundles __local_camel_producer__ and __local_camel_consumer__ on Fuse, one on each container __child1__ and __child2__ respectivity.

* __local_camel_producer__ : Scans a folder (/tmp/incoming) for incoming CSV files of the format "id, firstName, lastName, age". If a file is dropped with entries in the given format, each entry is read and transformed into a Person POJO and stored in the data grid

* __local_camel_consumer__ : Lets you query for a POJO using a RESTful interface and get back  a JSON representation of the Person POJO stored in the data grid for the given key

Prerequisites
-------------
1. JDK 1.7 
2. Maven 3.0
3. JBoss Fuse 6.1.0
4. _Optional_: JBoss Data Grid 6.4.0 Maven repository

Setup
-----
1. Download the Fuse binary __jboss-fuse-full-6.1.0.redhat-379.zip__ from either http://www.jboss.org/products or from Red Hat's Customer portal
2. Ensure that you have correctly setup JDG repos in your .m2/settings.xml. See the [official documentation] (https://access.redhat.com/documentation/en-US/Red_Hat_JBoss_Data_Grid/6.4/html-single/Getting_Started_Guide/index.html#chap-Install_and_Use_the_Maven_Repositories) on how to accomplish that.
3. Run `mvn clean package install` in the root folder of the quickstart
4. Set the following two environment variables in the same shell: 
  1. export __FUSE_INSTALL_PATH__ = _[Full path to the folder into where Fuse will be installed]_ 
  2. export __FUSE_BINARY_PATH__ = _[Full path to the Fuse binary file]_ 
5. While in the root folder of the quickstart, run `./setEverythingOnFuse.sh`

Testing
-------

* Testing the __local_camel_producer__: Run a command `echo "1,Bill,Gates,59" > /tmp/incoming/sample.csv`. If the file disappears in a second or two then the producer worked correctly and you can proceed to testing the consumer
* Testing the __local_camel_producer__: Open up a browser and hit the url http://127.0.0.1:8282/cache/get/1 (where 1 is the Id with which the Person instance was stored in the grid). If you get a JSON represetation back of the corresponding POJO, the testing was successful
