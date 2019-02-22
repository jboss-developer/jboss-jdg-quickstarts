Customizing Data Grid Service Deployments: Red Hat Data Grid for OpenShift
==========================================================================
**Author:** Galder Zamarreno  
**Technologies:** Infinispan, Data Grid, OpenShift  
**Summary:** Learn how to use the OpenShift `ConfigMap` API to customize Data Grid service deployments.
**Target Product:** Data Grid for OpenShift  
**Product Versions:** RHDG 7.3 or later

About This Quickstart
---------------------
This quickstart demonstrates how to deploy Data Grid clusters with custom configuration on OpenShift.

**Before You Begin:** Complete the steps in the [OpenShift Quickstart README](../../README.md) to set up an OpenShift cluster.

Evaluating the Custom Template
------------------------------
Open `user-datagrid-service-template.yaml` with any text editor and look for modifications in the template between lines commented with `# user config mod "start|end"`.  

The template customizes `datagrid-service` by:

- Exposing the REST endpoint over HTTP at port `8080` instead of HTTPS at port `8443`.
- Adding a config volume for the container at `/opt/datagrid/standalone/configuration/user`.
- Adding a ConfigMap named `datagrid-config`.
- Setting the `APPLICATION_USER` parameter to a value of `false` because user authentication is not required for HTTP access.

Importing the Custom Template
-----------------------------
Make the custom template available on OpenShift.
```bash
$ oc create -f user-datagrid-service-template.yaml
```

Creating a ConfigMap
--------------------
1. Create a ConfigMap named `datagrid-config`.
```bash
$ oc create configmap datagrid-config --from-file=configuration
```

  The ConfigMap uses the `standalone.xml` file in the `configuration` directory for this quickstart as the Data Grid for OpenShift configuration file.

2. Optionally export the ConfigMap to verify it.
```bash
$ oc export configmap datagrid-config
```

Deploying a Data Grid Cluster
-----------------------------
Deploying a `datagrid-service` cluster, rather than a single instance, verifies the custom configuration. If the configuration is not valid, the internal JGroups cluster does not form between the pods.

```bash
$ oc new-app datagrid-service \
    -p APPLICATION_NAME=datagrid-service-user-config \
    -p NUMBER_OF_INSTANCES=2 \
    -e USER_CONFIG_MAP=true
```

- `NUMBER_OF_INSTANCES=2` creates a Data Grid cluster with two pods.
- `USER_CONFIG_MAP=true` applies the `datagrid-config` ConfigMap to `datagrid-service`.

You do not specify credentials with the `APPLICATION_USER` and `APPLICATION_PASSWORD` parameters because the custom template disables mandatory authentication.

After you successfully deploy `datagrid-service`, export the template to verify the custom modifications.

```bash
$ oc export template datagrid-service
```

The `datagrid-service` template uses HTTP instead of HTTPS:

```yaml
kind: Service
  metadata:
    annotations:
      description: Provides a service for accessing the application over HTTP or Hot Rod protocol.
      service.alpha.openshift.io/serving-cert-secret-name: service-certs
    labels:
      application: ${APPLICATION_NAME}
  spec:
    ports:
    - name: hotrod
      port: 11222
      targetPort: 11222
    - name: http
      port: 8080
      targetPort: 8080
    selector:
      deploymentConfig: ${APPLICATION_NAME}
```

Building the Quickstart Application
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

2. Build the quickstart application.
```bash
$ mvn -s ../../../settings.xml clean package compile -DincludeScope=runtime
```
  Artifacts are built in the `target` directory.

3. Start the build on OpenShift.
```bash
$ oc start-build quickstart --from-dir=target/ --follow
```

Verifying Unauthenticated Access with Hot Rod
---------------------------------------------
1. Run the quickstart application.
```bash
$ oc run quickstart \
  --image=`oc get is quickstart -o jsonpath="{.status.dockerImageRepository}"` \
  --replicas=1 \
  --restart=OnFailure \
  --env APP_NAME=datagrid-service-user-config \
  --env JAVA_OPTIONS=-ea
```  
  The quickstart application connects to the Data Grid service and invokes a `PUT` operation that stores a `key='key-hotrod'/value='user-config'` pair and performs an assertion to verify the entry.

2. Verify the `PUT` operation completed successfully.
```
$ oc logs quickstart-${id} --tail=50
--- Connect to datagrid-service-user-config ---
...
--- Store key='key-hotrod'/value='user-config' pair ---
...
--- Retrieve key='key-hotrod' ---
--- Value is 'user-config' ---
```
  Where `${id}` is the unique ID for the pod. **TIP:** Use `oc get pods` to find the pod name.

Verifying Access with HTTP
--------------------------
1. Invoke a `POST` operation to store data via the REST API over HTTP.
```bash
$ oc exec \
  -it datagrid-service-user-config-0 \
  -- curl -v -X POST \
  -H 'Content-type: text/plain' \
  -d 'user-config' \
  datagrid-service-user-config:8080/rest/default/key-rest
```

  If the `POST` operation is successful, you should get output similar to the following:

  ```
  * About to connect() to datagrid-service-user-config port 8080 (#0)
  *   Trying 172.30.180.240...
  * Connected to datagrid-service-user-config (172.30.180.240) port 8080 (#0)
  > POST /rest/default/key-rest HTTP/1.1
  > User-Agent: curl/7.29.0
  > Host: datagrid-service-user-config:8080
  > Accept: */*
  > Content-type: text/plain
  > Content-Length: 11
  >
  * upload completely sent off: 11 out of 11 bytes
  < HTTP/1.1 200 OK
  < connection: keep-alive
  < etag: -1432381597
  < content-length: 0
  <
  * Connection #0 to host datagrid-service-user-config left intact
  ```

2. Invoke a `GET` operation to retrieve data via the REST API over HTTP.
```bash
$ oc exec \
  -it datagrid-service-user-config-0 \
  -- curl -v \
  datagrid-service-user-config:8080/rest/default/key-rest
```

  If the `GET` operation is successful, you should get output similar to the following:

  ```
  * About to connect() to datagrid-service-user-config port 8080 (#0)
  *   Trying 172.30.180.240...
  * Connected to datagrid-service-user-config (172.30.180.240) port 8080 (#0)
  > GET /rest/default/key-rest HTTP/1.1
  > User-Agent: curl/7.29.0
  > Host: datagrid-service-user-config:8080
  > Accept: */*
  >
  < HTTP/1.1 200 OK
  < connection: keep-alive
  < etag: -1432381597
  < last-modified: Thu, 1 Jan 1970 00:00:00 GMT
  < content-type: application/octet-stream
  < content-length: 11
  <
  * Connection #0 to host datagrid-service-user-config left intact
  ```

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
