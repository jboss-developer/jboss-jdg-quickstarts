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

**Before You Begin:** Complete the steps in the main `README` to set up an OpenShift cluster and create Data Grid for OpenShift services.

Building the Hello World Quickstart
-----------------------------------

1. Create a new binary build on OpenShift.
```bash
$ oc new-build \
    --binary \
    --strategy=source \
    --name=quickstart \
    -l app=quickstart \
    fabric8/s2i-java:2.3
```

2. Build the Hello World quickstart.
```bash
$ mvn -s ../../../settings.xml clean package compile -DincludeScope=runtime
```
  Artifacts are built in the `target` directory.

3. Start the build on OpenShift.
```bash
$ oc start-build quickstart --from-dir=target/ --follow
```

Running the Hello World Quickstart
----------------------------------
1. Invoke cache operations with the quickstart application.
```bash
$ oc run quickstart \
    --image=`oc get is quickstart -o jsonpath="{.status.dockerImageRepository}"` \
    --replicas=1 \
    --restart=OnFailure \
    --env APP_NAME=${appName} \
    --env SVC_DNS_NAME=${appName}-hotrod \
    --env JAVA_OPTIONS=-ea
```
  Where `${appName}` matches the application name that you specified when you created `cache-service` or `datagrid-service`.

2. Verify the cache operations completed successfully.
```
$ oc logs quickstart --tail=50
--- Connect to datagrid-service ---
...
--- Store key='hello'/value='world' pair ---
...
--- Retrieve key='hello' ---
--- Value is 'world' ---
```
  The preceding log messages show the Hello World quickstart connected to the Data Grid for OpenShift service and stored a `hello/world` key/value pair. The quickstart application also performs an assertion to ensure that the returned value is `world`.

  You've successfully completed this tutorial!

  Do one of the following:

  - Delete quickstart resources and continue using the project with RHDG for OpenShift.

    ```bash
    $ oc delete all --selector=run=quickstart || true
    $ oc delete imagestream quickstart || true
    $ oc delete buildconfig quickstart || true
    ```

  - Delete the project to remove all resources, for example:

    ```bash
    $ oc delete project my_project
    ```
