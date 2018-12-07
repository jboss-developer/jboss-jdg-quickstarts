Red Hat Data Grid (RHDG) for OpenShift
======================================
**Author:** Galder Zamarreno  
**Technologies:** Infinispan, Data Grid, OpenShift  
**Summary:** Learn how to run Data Grid on OpenShift. This directory contains a variety of quickstart tutorials from simple "Hello World" demonstrations to more advanced use cases.  
**Target Product:** Red Hat Data Grid  
**Product Versions:** RHDG 7.3 or later

System Requirements
-------------------
* Java 8.0 (Java SDK 1.8) or later.
* Maven 3.0 or later. You must also [configure Maven](https://github.com/jboss-developer/jboss-developer-shared-resources/blob/master/guides/CONFIGURE_MAVEN.md#configure-maven-to-build-and-deploy-the-quickstarts).
* OpenShift Container Platform 3.11 or later.
* An `oc` client in your `$PATH`.
* Red Hat customer account. You must have valid credentials to pull resources from _registry.redhat.io_.

Setting Up Minishift
--------------------
Install and configure minishift to create a local OpenShift cluster for working with these quickstart tutorials.

1. Download and install minishift.  
>The [Red Hat Container Development Kit](https://developers.redhat.com/products/cdk/overview/) includes minishift.

2. Clone this repository to your host.
3. Set your Red Hat customer account credentials with the `REDHAT_REGISTRY_USER` and `REDHAT_REGISTRY_PASSWORD` environment variables.
```bash
$ export REDHAT_REGISTRY_USER=username@redhat.com
$ export REDHAT_REGISTRY_PASSWORD=password
```
4. Run the following script to create a `datagrid-quickstart` profile and configure minishift (you need to run this script only once per host):
```bash
$ ./setup-minishift.sh
```
5. Run the following script to start minishift and create a pull secret with your Red Hat credentials:
```bash
$ ./start-minishift.sh
```
You should now have a locally running OpenShift cluster and can start using the quickstart tutorials.
