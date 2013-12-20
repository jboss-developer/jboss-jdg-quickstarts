JBoss Data Grid (JDG) Quickstarts
===========================

Introduction
-------------

JBoss Data Grid (JDG) is an open source data grid platform that offers multiple clustered modes, but its true value is observed while running in distributed mode where caches cluster together and expose a large memory heap. 

JBoss Data Grid offers two usage modes:

* _Library mode_  - This mode provides all the binaries required to build and deploy a custom runtime environment. The library usage mode allows local access to a single node in a distributed cluster. This usage mode gives the application access to data grid functionality within a virtual machine in the container being used. Supported containers include JBoss AS 7, JBoss Enterprise Application Platform 6 and Tomcat 7. 
* _Client-server mode_ - This mode provides a managed, distributed and clusterable data grid server. Applications can remotely access the data grid server using Hot Rod, memcached or REST client APIs., the Cache is stored in  a managed, distributed and clusterable data grid server.  Applications can remotely access the data grid server using Hot Rod, memcached or REST client APIs. This web application bundles only the HotRod client and communicates with a remote JBoss Data Grid (JDG) server. The JDG server is configured via the `standalone.xml` configuration file.

The quickstarts included in this distribution were written to demonstrate JBoss Data Grid functionality and features. They provide small, specific, working examples that can be used as a reference for your own project. 



Available Quickstarts 
---------------------

The following is a list of the currently available quickstarts. The table lists each quickstart name, the technologies it demonstrates, gives a brief description of the quickstart, and the level of experience required to set it up. For more detailed information about a quickstart, click on the quickstart name.
This distribution contains the following quickstarts:

| **Quickstart Name** | **Demonstrated Technologies** | **Description** |
|:-----------|:-----------|:-----------|
| [carmart](carmart/README.md) | Infinispan, CDI | Shows how to use Infinispan instead of a relational database. |
| [carmart-tx](carmart-tx/README.md) | Infinispan, CDI, Transactions | Shows how to use Infinispan instead of a relational database with transactions enabled.|
| [helloworld-jdg](helloworld-jdg/README.md) | Infinispan, CDI | Shows how to use Infinispan in clustered mode, with expiration enabled.|
| [hotrod-endpoint](hotrod-endpoint/README.md) | Infinispan, Hot Rod | Demonstrates how to use Infinispan remotely using the Hot Rod protocol. |
| [remote-query](remote-query/README.md) | Infinispan, Hot Rod, Remote Query | Demonstrates how to query Infinispan remotely using the Hot Rod protocol. |
| [memcached-endpoint](memcached-endpoint/README.md) | Infinispan, Memcached | Demonstrates how to use Infinispan remotely using the Memcached protocol. |
| [rest-endpoint](rest-endpoint/README.md) | Infinispan, REST | Demonstrates how to use Infinispan remotely using the REST protocol. |
