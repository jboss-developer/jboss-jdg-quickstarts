JBoss Data Grid Quickstarts
===========================

These are the quickstarts for JBoss Data Grid.

This distribution contains the following examples:

* `hotrod-endpoint` - simple example using HotRod client to connect to JDG server
* `memcached-endpoint` - simple example using Memcached client to connect to JDG server
* `rest-endpoint` - simple example using REST to connect to JDG server
* `carmart` - simple web application running either in library mode or using HotRod client in client-server mode
* `carmart-transactional` - simple transactional web application running in library mode

These quickstarts also require that the user has installed the associated Maven repository available as

   jboss-datagrid-maven-repository-$VERSION.zip

Follow the instructions within the above archive to install the Maven repository in your environment.

The simplest method is to just unpack the Maven repository to a directory on your filesystem and then
specify the full path to this directory when building the examples as follows:

    mvn -Ddatagrid.maven.repo=file:///path/to/unpacked/maven/repository clean package
