Creating Caches with the Cache Service: Red Hat Data Grid for OpenShift
=======================================================================
**Author:** Galder Zamarreno  
**Technologies:** Infinispan, Data Grid, OpenShift  
**Summary:** Learn how to create permanent caches with the Cache Service.
**Target Product:** Data Grid for OpenShift  
**Product Versions:** RHDG 7.3 or later

About This Quickstart
---------------------
This quickstart demonstrates how to connect to the `cache-service` with a lightweight Java client that runs on OpenShift. You then use the client to create a permanent cache.

Permanent caches survive between application restarts. You only need to create a permanent cache once. However, data in the cache is not persisted between restarts unless you add storage.

**Before You Begin:** Complete the steps in the [OpenShift Quickstart README](../../README.md) to set up an OpenShift cluster and create Data Grid for OpenShift services.

Configuring Authentication
--------------------------
The quickstart application must authenticate with `cache-service`.

1. Open `CreateCache.java` for editing.

2. In the `CreateCache` class, update the following values with the credentials you specified when you created `cache-service`:
```java
private static final String USER = "test";
private static final String PASSWORD = "changeme";
```

3. Save and close `CreateCache.java`.

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

Creating Permanent Caches
-------------------------
1. Create a custom cache with the quickstart application.
```bash
$ oc run quickstart \
   --image=`oc get is quickstart -o jsonpath="{.status.dockerImageRepository}"` \
   --replicas=1 \
   --restart=OnFailure \
   --env APP_NAME=${appName} \
   --env CMD=create-cache \
   --env JAVA_OPTIONS=-ea
```
  Where `${appName}` matches the application name that you specified when you created `cache-service`.

  **TIP:** Use `oc get statefulsets` to retrieve the application name.

  The `create-cache` command calls the following `createCache` method in the quickstart application:

  ```java
  private static void createCache(ConfigurationBuilder cfg) {
     final RemoteCacheManager remote = new RemoteCacheManager(cfg.build());

     final String cacheName = "custom";

     final RemoteCache<?, ?> createdCache = remote.administration()
        .withFlags(CacheContainerAdmin.AdminFlag.PERMANENT)
        .getOrCreateCache(cacheName, "default");

  }
  ```

  The preceding code:
  - Instantiates `RemoteCacheManager` to connect to the Cache Service.
  - Creates a cache named `custom` using the default cache definition. Note that you cannot create custom caches with `cache-service`. You can only create caches as copies of the default cache definition.
  - Includes the `AdminFlag.PERMANENT` flag to creates a permanent cache. If you do not include that flag, you create an ephemeral cache.
  - Calls the `getOrCreateCache` method in the `RemoteCacheManagerAdmin` interface that returns the cache name instead of throwing an exception if the cache already exists.

2. Verify the cache is created successfully.
```
$ oc logs quickstart --tail=50
...
--- Connect to cache-service ---
...
--- Create cache in cache-service ---
...
--- Cache 'custom' created in 'cache-service' ---
```

3. Delete the quickstart pod.
```bash
$ oc delete all --selector=run=quickstart || true
```

Verifying the Cache is Permanent
--------------------------------
1. Invoke cache operations with the quickstart application.
```bash
$ oc run quickstart \
   --image=`oc get is quickstart -o jsonpath="{.status.dockerImageRepository}"` \
   --replicas=1 \
   --restart=OnFailure \
   --env APP_NAME=${appName} \
   --env CMD=get-cache \
   --env JAVA_OPTIONS=-ea
```
  The `get-cache` command invokes a `PUT` operation that stores a `key='hello'/value='world'` pair in the `custom` cache and performs an assertion to verify the entry.

2. Verify the cache operations completed successfully.
```
$ oc logs quickstart --tail=50
...
--- Connect to cache-service ---
...
--- Store key='hello'/value='world' pair in cache 'custom'---
...
--- Retrieve key='hello' from cache 'custom' ---
--- Value is 'world' ---
```

3. Delete the quickstart pod.
```bash
$ oc delete all --selector=run=quickstart || true
```

4. Scale the `cache-service` as follows:

    1. Check the number of replicas for the `cache-service`.
    ```bash
    $ oc get statefulsets
    NAME                         DESIRED   CURRENT   AGE
    cache-service                1         1         1h
    ```

    2. Scale the `cache-service` to `0` replicas.
    ```bash
    $ oc scale statefulsets cache-service --replicas=0
    statefulset "cache-service" scaled
    ```

    3. Check that there are no replicas.
    ```bash
    $ oc get statefulsets
    NAME                         DESIRED   CURRENT   AGE
    cache-service                0         0         1h
    ```

    If there are no replicas for the `cache-service`, no instances of the application are running. If the cache is permanent, the `custom` cache you created becomes available again when the service scales back up.

    4. Scale the `cache-service` up.
    ```bash
    $ oc scale statefulsets cache-service --replicas=1
    statefulset "cache-service" scaled
    ```

    5. Watch the pod and wait until the service starts running.
    ```bash
    $ oc get pods -w
    ```

5. Invoke cache operations with the `get-cache` command again.
```bash
$ oc run quickstart \
   --image=`oc get is quickstart -o jsonpath="{.status.dockerImageRepository}"` \
   --replicas=1 \
   --restart=OnFailure \
   --env APP_NAME=${appName} \
   --env CMD=get-cache \
   --env JAVA_OPTIONS=-ea
```

6. Verify the cache operations.
```
$ oc logs quickstart --tail=50
...
--- Connect to cache-service ---
...
--- Store key='hello'/value='world' pair in cache 'custom'---
 ...
--- Retrieve key='hello' from cache 'custom' ---
--- Value is 'world' ---
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
