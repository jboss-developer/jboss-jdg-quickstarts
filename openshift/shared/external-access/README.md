External Access: Red Hat Data Grid for OpenShift
============================================
**Author:** Galder Zamarreno  
**Technologies:** Infinispan, Data Grid, OpenShift  
**Summary:** Learn how to access Data Grid pods externally.  
**Target Product:** Data Grid for OpenShift  
**Product Versions:** RHDG 7.3 or later

About This Quickstart
---------------------
This quickstart demonstrates how to access `cache-service` and `datagrid-service` pods externally.

To access pods externally via Hot Rod, you build and run a simple Java client for OpenShift. For external HTTPS access, you use the `curl` command.

In both cases, you authenticate with the Data Grid for OpenShift service and then store and retrieve a sample entry from your host outside the pod where the service is running.

**Before You Begin:** Read through the main `README` for the OpenShift quickstarts.

Creating Routes
---------------
Routes provide access to Data Grid endpoints.
1. Create `passthrough` routes for Hot Rod and HTTPS endpoints.
  * Hot Rod
  ```bash
  $ oc create route passthrough ${appName}-hotrod-route \
    --port=11222 \
    --service ${appName}-hotrod
  ```
  * HTTPS
  ```bash
  $ oc create route passthrough ${appName}-https-route \
    --port=8443 \
    --service ${appName}-https
  ```
  Where `${appName}` matches the application name that you specified when you created `cache-service` or `datagrid-service`.  

    **TIP:** Use `oc get statefulsets` to retrieve the application name.

2. Verify the routes are created successfully.
  ```bash
  $ oc get routes
  ```

3. Note the hostnames for each endpoint:

  ```
  NAME                         HOST/PORT                                                       ...
  cache-service-hotrod-route   cache-service-hotrod-route-myproject.192.168.42.80.nip.io       ...    
  cache-service-https-route    cache-service-https-route-myproject.192.168.42.80.nip.io        ...     
  ```

Accessing Pods Externally with Hot Rod
--------------------------------------
1. Build the Java client for OpenShift.
  ```bash
  $ mvn -s ../../../settings.xml clean package
  ```

  Client artifacts are built in the `target` directory.

2. Run the client.
  ```bash
  $ mvn -s ../../../settings.xml exec:java -Dexec.args="${appName}"
  ```

  You should see output such as the following:

  ```
  --- Host is: myapp-hotrod-route-myproject.192.168.0.0.nip.io
  --- Connect to cache-service ---
  ...
  --- Store key='external'/value='access' pair ---
  --- Retrieve key='external' ---
  --- Value is 'access' ---
  ```

Accessing Pods Externally with HTTPS
------------------------------------
1. Retrieve the public certificate for the CA, `tls.crt`, from the `service-certs` secret.
  ```bash
  $ oc get secret service-certs \
  -o jsonpath='{.data.tls\.crt}' \
  | base64 > tls.crt
  ```

2. Invoke a `PUT` operation to store a value of `world` in a key named `hello`.
  ```bash
  curl -X PUT \
  -k \
  -u ${user}:${password} \
  --cacert tls.crt \
  -H 'Content-type: text/plain' \
  -d 'world' \
  https://${routeHost}/rest/default/hello
  ```

  Where:
  - `${user}` and `${password}` match the credentials that you specified when you created `cache-service` or `datagrid-service`.
  - `${routeHost}` matches the hostname of the route that exposes the `cache-service` or `datagrid-service` REST endpoint, for example: `cache-service-https-route-myproject.192.168.42.80.nip.io`.

3. Invoke a `GET` operation to verify the entry.
  ```bash
  curl -X GET  \
  -k  \
  -u ${user}:${password}  \
  --cacert tls.crt  \
  https://${routeHost}/rest/default/hello
  ```

  The `GET` operation returns the value of `world`.

  You've successfully completed this tutorial!

  Delete the project to remove all resources, for example:
  ```bash
  $ oc delete project my_project
  ```
