Hello World: Red Hat Data Grid for OpenShift
============================================
**Author:** Galder Zamarreno  
**Technologies:** Infinispan, Data Grid, OpenShift  
**Summary:** Quickly verify Data Grid service deployments.  
**Target Product:** Data Grid for OpenShift  
**Product Versions:** RHDG 7.3 or later

About This Quickstart
---------------------
This quickstart provides a lightweight Java application that connects to Data Grid for OpenShift services and stores, retrieves, then verifies a sample key/value pair.

You can use this quickstart with both `cache-service` and `datagrid-service` deployments.

Before You Begin
----------------
* Ensure you meet system requirements.
* Install and set up `minishift`.

See the main `README` in this repository for details.

Deploying Data Grid for OpenShift Services
------------------------------------------
Deploy either the `cache-service` and `datagrid-service`. See the [Red Hat Data Grid for OpenShift](https://access.redhat.com/documentation/en-us/red_hat_data_grid/7.3/html-single/data_grid_for_openshift/) 7.3 documentation.

Building the Hello World Quickstart
-----------------------------------

1. Create a new binary build on OpenShift.
```bash
$ oc new-build \
    --binary \
    --strategy=source \
    --name=${demo} \
    -l app=${demo} \
    fabric8/s2i-java:2.3
```
2. Build the Hello World quickstart.
```bash
$ mvn -s ../../../settings.xml clean package compile -DincludeScope=runtime
```
3. Start the build on OpenShift.
```bash
$ oc start-build ${demo} --from-dir=target/ --follow
```

Running the Hello World Quickstart
----------------------------------

Run the Hello World quickstart as follows:

```bash
$ oc run ${demo} \
    --image=`oc get is ${demo} -o jsonpath="{.status.dockerImageRepository}"` \
    --replicas=1 \
    --restart=OnFailure \
    --env APP_NAME=${appName} \
    --env SVC_DNS_NAME=${svcDnsName} \
    --env JAVA_OPTIONS=-ea
```

Verifying the Hello World Quickstart
------------------------------------

1. Log in to OpenShift.
2. Navigate to the pod for the Hello World quickstart.
3. Open the logs for the pod.

If you built and ran the Hello World quickstart successfully, you should find log messages such as the following:

```
--- Connect to datagrid-service ---
    ...
--- Store key='hello'/value='world' pair ---
    ...
--- Retrieve key='hello' ---
--- Value is 'world' ---
```

The preceding messages indicate that the Hello World quickstart connected to the `datagrid-service` and stored a `hello/world` key/value pair. The Hello World quickstart also includes an assertion to ensure that the returned value is `world`.
