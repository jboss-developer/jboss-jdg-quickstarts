Cross-Site Replication: Red Hat Data Grid (RHDG) for OpenShift
==============================================================
**Authors:** Galder Zamarreno, Don Naro  
**Technologies:** Infinispan, Red Hat Data Grid, Red Hat OpenShift  
**Summary:** Learn how to run back up data from one Data Grid cluster to another.  
**Target Product:** Red Hat Data Grid  
**Product Versions:** RHDG 7.3 or later

Technical Preview
-----------------
**IMPORTANT:** Cross-site replication with Data Grid 7.3 on OpenShift is a Technical Preview feature and is not intended for use in production environments.

Data Grid support for cross-site replication on OpenShift is in the testing phase and planned for a future release. For more information, see the [Data Grid 7.3 Release Notes](https://access.redhat.com/documentation/en-us/red_hat_data_grid/7.3/html-single/red_hat_data_grid_7.3_release_notes/).

About This Quickstart
---------------------
Cross-site replication ensures data redundancy across clusters that typically run in different physical locations.

In this quickstart you use two minishift profiles, `xsite-a` (**Site A**) and `xsite-b` (**Site B**), to create separate OpenShift clusters. You then deploy `datagrid-service` with custom configuration that enables data replication between **Site A** and **Site B**.

To demonstrate how Data Grid replicates data between sites, you run a simple Java application to store a sample key/value entry in **Site A** and then retrieve the entry from **Site B**.

When you're finished, review the [Configuration Reference](#config_ref) for details on how Data Grid uses the JGroups RELAY2 protocol for cross-site replication.

Setting Up Sites in Minishift
-----------------------------
**Before You Begin:** Review the system requirements and complete the procedures in _Setting Up Minishift_ in the [OpenShift Quickstart README](../../README.md).

1. Run the following script to create an `xsite-a` and an `xsite-b` profile and configure minishift:
```bash
$ ./setup-minishift.sh
```
> **Hint:** Pass the `VMDRIVER` variable to set the hypervisor that minishift uses (`vm-driver`). For example, if you want to use VirtualBox embedded drivers.

2. Run the following script to start minishift and create a pull secret with your Red Hat credentials:
```bash
$ ./start-minishift.sh
```
  You should now have two locally running OpenShift clusters. One for **Site A** and one for **Site B**.

3. Verify both clusters are running. For example:
```bash
$ ps aux | grep -i VBoxHeadless
```
  You should see two separate processes running for `xsite-a` and `xsite-b`.

  **NOTE:** Run the following command in any new terminal windows to ensure you use the correct `oc` binary:
  ```bash
  $ eval $(minishift oc-env)
  ```

Creating a Cross-Site Replication Service
-----------------------------------------
Create a service that lets backup sites communicate with each other via the `jgroups-tcp-relay` socket binding at port `8660`.
```bash
$ oc create -f xsite-service.yaml
```

**NOTE:** `xsite-service.yaml` uses a fixed port for the cross-site replication service. If you do not use a fixed port, you must create the cross-site replication service first so you can specify the correct port for `jgroups-tcp-relay` in the Data Grid configuration.

Importing the Custom Template
-----------------------------
1. Make the custom template available on OpenShift.
```bash
$ oc create -f xsite-datagrid-service-template.yaml
```
2. Create a ConfigMap named `datagrid-config`.
```bash
$ oc create configmap datagrid-config --from-file=configuration
```
  The ConfigMap uses the `standalone.xml` file in the `configuration` directory for this quickstart as the Data Grid for OpenShift configuration file.

3. Optionally export the ConfigMap to verify it.
```bash
$ oc export configmap datagrid-config
```

Setting Up the Other Site
-------------------------
1. Check your active profile, for example:
```bash
$ minishift profile list
-xsite-a    Running     (Active)
-xsite-b    Running
```
2. Switch your active profile to the other site, for example:
```bash
$ minishift profile set xsite-b
Profile 'xsite-b' set as active profile.
```
3. Create the cross-site replication service and custom Data Grid resources.
```bash
$ oc create -f xsite-service.yaml
```
```bash
$ oc create -f xsite-datagrid-service-template.yaml
```
```bash
$ oc create configmap datagrid-config --from-file=configuration
```

Gathering Information
---------------------
You need to retrieve information for both **Site A** and **Site B** before you deploy `datagrid-service`.

Do the following on both sites:

1. Find the external IP address for the site.
```bash
$ minishift ip
192.0.2.0
```

2. Get the node port for the cross-site replication service.
```bash
$ oc get svc/datagrid-service-xsite
NAME                     TYPE       CLUSTER-IP       EXTERNAL-IP   PORT(S)          AGE
datagrid-service-xsite   NodePort   172.30.195.135   <none>        8660:32660/TCP   2m
```
  In the preceding command output, the node port is `32660`.

Deploying Data Grid Services
----------------------------
Create a new `datagrid-service` application on the active site.
```bash
$ oc new-app datagrid-service \
  -p APPLICATION_NAME=datagrid-service-xsite-hello-world \
  -e USER_CONFIG_MAP=true \
  -e SCRIPT_DEBUG=true \
  -e JAVA_OPTS_APPEND="-Djboss.bind.ext_address=${extAddr}
    -Djboss.bind.ext_port=${nodePort}
    -Djboss.relay.site=${siteName}
    -Djboss.relay.global_cluster=${discovery}"
```
  Where:

  - `${extAddr}` is the external IP for the site.
  - `${nodePort}` is the node port for the `datagrid-service-xsite` service.
  - `${siteName}` is either `SiteA` or `SiteB`.
  - `${discovery}` maps external IPs to node ports as follows:

  `${extAddr-SiteA}[${extPort-SiteB}],${extAddr-SiteB}[${extPort-SiteA}]`
    For example: `192.0.2.0[32660],192.0.2.1[32660]`

  **NOTE:** The launch script does not generate keystores when you create Data Grid with custom configuration. To enable authentication, you must set up keystores for `datagrid-service` manually and include the following parameters:

  ```
  -p APPLICATION_USER=${username}
  -p APPLICATION_PASSWORD=${password}
  ```

  The command output is similar to the following:

  ```
  --> Deploying template "myproject/datagrid-service" to project myproject

     Red Hat Data Grid Service
     ---------
     Red Hat Data Grid is an in-memory, distributed key/value store.

     * With parameters:
        * Application Name=datagrid-service-xsite-hello-world
        * IMAGE=registry.redhat.io/jboss-datagrid-7/datagrid73-openshift
        * Number of Instances=1
        * Total Memory=512
        * Storage Capacity=1

  --> Creating resources ...
      secret "datagrid-service-xsite-hello-world" created
      service "datagrid-service-xsite-hello-world-ping" created
      service "datagrid-service-xsite-hello-world" created
      statefulset.apps "datagrid-service-xsite-hello-world" created
  --> Success
      Application is not exposed. You can expose services to the outside world by executing one or more of the commands below:
       'oc expose svc/datagrid-service-xsite-hello-world-ping'
       'oc expose svc/datagrid-service-xsite-hello-world'
      Run 'oc status' to view your app.
  ```

  `datagrid-service` should now be configuring and running.

  **Next Step:** Switch your active profile to the other site and create a new `datagrid-service` application.

Confirming a Cross-Site View is Formed
--------------------------------------
After you deploy Data Grid on both **Site A** and **Site B** the clusters should form a cross-site view, which is to say the clusters establish communication through the transport channel for data replication.

Do the following on both **Site A** and **Site B**:

```bash
$ oc logs datagrid-service-xsite-hello-world-0 \
  | grep "Received new x-site view"
```

Output on **Site A**:
```
INFO  [org.infinispan.remoting.transport.jgroups.JGroupsTransport]
(jgroups-3,rid-service-xsite-app-0) ISPN000439:
Received new x-site view: [SiteA]
INFO  [org.infinispan.remoting.transport.jgroups.JGroupsTransport]
(jgroups-4,_ice-xsite-hello-world-0:SiteB) ISPN000439:
Received new x-site view: [SiteA, SiteB]
```

Output on **Site B**:
```
INFO  [org.infinispan.remoting.transport.jgroups.JGroupsTransport]
(jgroups-3,rid-service-xsite-app-0) ISPN000439: Received new x-site view: [SiteB]
INFO  [org.infinispan.remoting.transport.jgroups.JGroupsTransport]
(jgroups-4,_ice-xsite-hello-world-0:SiteB) ISPN000439:
Received new x-site view: [SiteB, SiteA]
```

Building and Uploading the Quickstart
-------------------------------------
Do the following on both **Site A** and **Site B**:

1. Create the build artifacts in the `target` directory.
```bash
$ mvn -s ../../../settings.xml clean package compile -DincludeScope=runtime
```
2. Upload the build to your OpenShift namespace.
```bash
$ oc new-build \
  --binary \
  --strategy=source \
  --name=quickstart \
  -l app=quickstart \
  fabric8/s2i-java:2.3
```
3. Build the quickstart application.
```bash
$ oc start-build quickstart --from-dir=target/ --follow
```

Putting Data into Site A
------------------------
1. Make **SiteA** your active profile.
```
$ minishift profile set xsite-a
Profile 'xsite-a' set as active profile.
```
2. Run the quickstart to put sample data into `datagrid-service` on site **Site A**.
```bash
$ oc run quickstart \
  --image=`oc get is quickstart -o jsonpath="{.status.dockerImageRepository}"` \
  --replicas=1 \
  --restart=OnFailure \
  --env SVC_DNS_NAME=datagrid-service-xsite-hello-world \
  --env CMD=put-data \
  --env JAVA_OPTIONS=-ea
```
3. Verify the `PUT` operation completed successfully.
```
$ oc logs quickstart-${id} --tail=50
--- Connect to datagrid-service-xsite-hello-world ---
...
--- Store key='cross'/value='site' pair ---
```
  Where `${id}` is the unique ID for the pod. **TIP:** Use `oc get pods` to find the pod name.

Getting Data from Site B
------------------------
1. Make **SiteB** your active profile.
```
$ minishift profile set xsite-b
Profile 'xsite-b' set as active profile.
```
2. Run the quickstart to get the sample data from `datagrid-service` on site **Site B**.
```bash
$ oc run quickstart \
  --image=`oc get is quickstart -o jsonpath="{.status.dockerImageRepository}"` \
  --replicas=1 \
  --restart=OnFailure \
  --env SVC_DNS_NAME=datagrid-service-xsite-hello-world \
  --env CMD=get-data \
  --env JAVA_OPTIONS=-ea
```
3. Verify the `GET` operation completed successfully.
```
$ oc logs quickstart-${id} --tail=50
--- Connect to datagrid-service-xsite-hello-world ---
...
--- Retrieve key='cross' ---
```
  Where `${id}` is the unique ID for the pod. **TIP:** Use `oc get pods` to find the pod name.

Cleaning the Namespace
----------------------
When you're done with this tutorial, either delete the project to remove all resources or delete specific resources only. Do this on both **Site A** and **Site B**.

To delete the project, run:
```bash
$ oc delete project myproject
```

To delete resources for `datagrid-service`, do the following:
```bash
$ oc delete all,secrets,sa,templates,configmaps,daemonsets,clusterroles, \
rolebindings,serviceaccounts --selector=template=datagrid-service || true
$ oc delete configmap datagrid-config || true
$ oc delete template datagrid-service || true
$ oc delete service datagrid-service-xsite || true
```

To delete resources for the `quickstart` application, do the following:
```bash
$ oc delete all --selector=run=quickstart || true
$ oc delete imagestream quickstart || true
$ oc delete buildconfig quickstart || true
```

<a name="config_ref"></a>
Examining the Custom Template and Configuration
-----------------------------------------------
1. Open `xsite-datagrid-service-template.yaml` with any text editor.
2. Search for lines commented with `xsite mod:start` to find modifications in the template.  

  The template customizes `datagrid-service` by:

  - Adding a config volume for the container at `/opt/datagrid/standalone/configuration/user`.
  - Adding a ConfigMap named `datagrid-config`.

3. Navigate to the `configuration` directory for this quickstart and open `standalone.xml` with any text editor and review the following sections:

  **Cache Container**

  Locate the embedded cache container named `clustered`. It has two cache definitions:

  - default cache
  - a cache named `cross-site` that configures **Site A** and **Site B** as backups.

  The cache container also includes a JGroups transport channel named `cluster`. This channel configures the protocol stack that Data Grid clusters use to communicate.
  ```
  <cache-container name="clustered" default-cache="default">
      <transport channel="cluster"/>
      <global-state/>
      <distributed-cache name="default"/>
      <distributed-cache name="cross-site">
          <backups>
              <backup site="SiteA" strategy="ASYNC" timeout="30000"/>
              <backup site="SiteB" strategy="ASYNC" timeout="30000"/>
          </backups>
      </distributed-cache>
  </cache-container>
  ```

  **Endpoints**

  Next, find the endpoint subsystem. This configuration allows `datagrid-service` pods running in **Site A** and **Site B** to communicate.
  ```
  <subsystem xmlns="urn:infinispan:server:endpoint:9.4">
  ```
  The `hotrod-connector` enables the Hot Rod endpoint for the `clustered` cache container using a socket binding named `hotrod-internal`.
  ```
  <hotrod-connector
    name="hotrod-internal"
    socket-binding="hotrod-internal"
    cache-container="clustered" />
  ```
  The `rest-connector` enables the REST endpoint for the `clustered` cache container using a socket binding named `rest`. Authentication is configured so that credentials are stored in the `ApplicationRealm` and retrieved with the `BASIC` method.
  ```
  <rest-connector
    name="rest"
    socket-binding="rest"
    cache-container="clustered">
      <authentication
        security-realm="ApplicationRealm"
        auth-method="BASIC"/>
  </rest-connector>
  ```

  **JGroups Protocol Stack**

  Now locate the JGroups subsystem.
  ```
  <subsystem xmlns="urn:infinispan:server:jgroups:9.4">
  ```
  The default JGroups channel is `cluster`, which configures Data Grid clusters to use the TCP protocol stack.
  ```
  <channels default="cluster">
     <channel name="cluster" stack="tcp"/>
     ...
  ```
  The JGroups subsystem also declares a channel named `global`. This channel enables Data Grid clusters to form cross-site views with the `relay-global` stack that uses the RELAY2 protocol.
  ```
  <channel name="global" stack="relay-global"/>
  ```
  Both the TCP and UDP protocol stacks contain `openshift.DNS_PING` using a socket-binding named `jgroups-mping`. DNS_PING is the discovery mechanism that enables nodes to form clusters.
  ```
  <protocol type="openshift.DNS_PING"
    socket-binding="jgroups-mping"/>
  ```
  The TCP protocol stack configures **Site A** and **Site B** for the RELAY2 protocol and specifies that the sites communicate with each other using the `relay-global` stack with the `global` JGroups channel.
  ```
  <relay site="${jboss.relay.site:stage}">
      <remote-site name="SiteA" stack="relay-global" cluster="global"/>
      <remote-site name="SiteB" stack="relay-global" cluster="global"/>
      <property name="relay_multicasts">false</property>
        <property name="max_site_masters">1000</property>
  </relay>
  ```
  The configuration for the `relay-global` protocol stack is then defined after the TCP protocol stack.
  ```
  <stack name="relay-global">
  ```

  **Socket Bindings**

  Find the socket binding group.
  ```
  <socket-binding-group name="standard-sockets" ...
  ```
  The `hotrod-internal` and `rest` sockets configure where the endpoints are exposed.
  ```
  <socket-binding name="hotrod-internal" port="11222"/>
  ...
  <socket-binding name="rest" port="8080"/>
  ```
  The `jgroups-mping` Multicast PING socket configures cluster discovery.
  ```
  <socket-binding name="jgroups-mping"
    port="0"
    multicast-address="${jboss.default.multicast.address:234.99.54.14}"
    multicast-port="45700"/>
  ```
  The `jgroups-tcp-relay` socket configures the TCP protocol in the `relay-global` stack.
  ```
  <socket-binding name="jgroups-tcp-relay" port="8660" />
  ```
  Data Grid clusters use the `relay-global` protocol stack through port `8660` to form cross-site views and replicate data to each other.

For more information, see the following:

* [Cross-Site Replication](https://access.redhat.com/documentation/en-us/red_hat_data_grid/7.3/html-single/red_hat_data_grid_user_guide/#x_site_replication) in the _Red Hat Data Grid User Guide_.
* [Relaying between multiple sites (RELAY2)](http://www.jgroups.org/manual/#Relay2Advanced) in the _Reliable group communication with JGroups_ guide.
