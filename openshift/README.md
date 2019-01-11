Red Hat Data Grid (RHDG) for OpenShift
======================================
**Technologies:** Infinispan, Data Grid, OpenShift  
**Summary:** Learn how to run Data Grid on OpenShift. This directory contains several quickstart tutorials from simple "Hello World" demonstrations to more advanced use cases.  
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
4. Run the following script to create a `datagrid-quickstart` profile and configure minishift (only once per host):
```bash
$ ./setup-minishift.sh
```
5. Run the following script to start minishift and create a pull secret with your Red Hat credentials:
```bash
$ ./start-minishift.sh
```

6. Add the `oc` binary to your `PATH`. This configures your shell to use the `oc` binary that Minishift copies to your host.
```bash
$ eval $(minishift oc-env)
```
You should now have a locally running OpenShift cluster and can start using the quickstart tutorials.

Running the Data Grid for OpenShift Quickstarts
-----------------------------------------------
1. Clone this repository and navigate to the quickstart directory.
```bash
$ cd ${repoHome}/openshift/shared/hello-world
```

2. Create a new project, for example:
```bash
$ oc new-project hello-world
```

3. Ensure a _registry.redhat.io_ pull secret is in the project.
```bash
$ oc get secrets
```

4. Create a new RHDG for OpenShift service.

   1. Confirm that `cache-service` or `datagrid-service` are available.
   ```bash
   $ oc get templates | grep 'cache-service\|datagrid-service'
   ```

    See the [Red Hat Data Grid for OpenShift](https://access.redhat.com/documentation/en-us/red_hat_data_grid/7.3/html-single/data_grid_for_openshift/) documentation for more information.

   2. Deploy `cache-service` or `datagrid-service`. Substitute appropriate values for each variable in the following commands:

   .`cache-service`
   ```bash
   $ oc new-app cache-service \
     -p APPLICATION_USER=${user} \
     -p APPLICATION_PASSWORD=${password} \
     -p APPLICATION_NAME=${appName}
   ```

   .`datagrid-service`
   ```bash
   $ oc new-app datagrid-service \
     -p APPLICATION_USER=${user} \
     -p APPLICATION_PASSWORD=${password} \
     -p APPLICATION_NAME=${appName}
   ```

5. Follow the procedures in the quickstart `README` to complete the tutorial.
