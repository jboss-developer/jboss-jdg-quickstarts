online-services: Example of using JDG Caching Service on OpenShift
==================================================================
Author: Sebastian Łaskawiec
Level: Intermediate
Technologies: Infinispan, OpenShift
Summary: The `online-services` quickstart demonstrates how to connect to a deployed JDG Online Services
Target Product: JDG
Product Versions: JDG 7.2

What is it?
-----------

The `online-services` quickstart demonstrates how to connect to to a deployed JDG Online Services.

Prerequisites
-------------

In order to run this project one needs to have an access to OpenShift installation with JDG Caching Service installed. Follow [the documentation](https://github.com/jboss-container-images/jboss-dataservices-image) how to do it.

System requirements
-------------------

All you need to build this project is Java 8.0 (Java SDK 1.8) or better, Maven 3.0 or better and OpenShift 3 client.

Configure Maven
---------------

If you have not yet done so, you must [Configure Maven](https://github.com/jboss-developer/jboss-developer-shared-resources/blob/master/guides/CONFIGURE_MAVEN.md#configure-maven-to-build-and-deploy-the-quickstarts) before testing the quickstarts.

Build the Quickstart
--------------------

Type this command to build the quickstart:

        mvn clean package

Run the Quickstart
------------------

Run the following command

        mvn fabric8:run

This will deploy this service into OpenShift and connect to a default deployed Online Service. The plugin uses already configured and connected
 `oc` or `kubectl` client, so make sure you are already authenticated and proper Project has been selected.

The expected output should be displayed on the console (the same that was used to invoke `mvn fabric8:run`).
Here is an example of the command's output:

        ...
        [INFO] <<< fabric8-maven-plugin:3.5.33:run (default-cli) < install @ caching-service <<<
        [INFO]
        [INFO]
        [INFO] --- fabric8-maven-plugin:3.5.33:run (default-cli) @ caching-service ---
        [INFO] F8: Using OpenShift at https://127.0.0.1:8443/ in namespace myproject with manifest /home/slaskawi/work/infinispan/jboss-jdg-quickstarts/caching-service/target/classes/META-INF/fabric8/openshift.yml
        [INFO] OpenShift platform detected
        [INFO] Using project: myproject
        [INFO] Using project: myproject
        [INFO] Creating a DeploymentConfig from openshift.yml namespace myproject name caching-service
        [INFO] Created DeploymentConfig: target/fabric8/applyJson/myproject/deploymentconfig-caching-service.json
        [INFO] F8: HINT: Use the command `oc get pods -w` to watch your pods start up
        [INFO] F8: Scaling DeploymentConfig myproject/caching-service to replicas: 1
        [INFO] F8: Watching pods with selector LabelSelector(matchExpressions=[], matchLabels={app=caching-service, provider=fabric8, group=org.jboss.quickstarts.jdg}, additionalProperties={}) waiting for a running pod...
        [INFO] F8:[NEW] caching-service-1-z7ll1 status: Pending
        [INFO] F8:[NEW] caching-service-1-z7ll1 status: Running Ready
        [INFO] F8:[NEW] Tailing log of pod: caching-service-1-z7ll1
        [INFO] F8:[NEW] Press Ctrl-C to scale down the app and stop tailing the log
        [INFO] F8:[NEW]
        [INFO] F8: Starting the Java application using /opt/run-java/run-java.sh ...
        [INFO] F8: exec java -javaagent:/opt/jolokia/jolokia.jar=config=/opt/jolokia/etc/jolokia.properties -cp . -jar /deployments/caching-service-7.2.0-redhat-SNAPSHOT.jar
        [INFO] F8: Nov 20, 2017 10:29:04 AM org.infinispan.client.hotrod.impl.protocol.Codec20 readNewTopologyAndHash
        [INFO] F8: INFO: ISPN004006: caching-service-app-hotrod:11222 sent new topology view (id=1, age=0) containing 1 addresses: [172.17.0.2:11222]
        [INFO] F8: Nov 20, 2017 10:29:04 AM org.infinispan.client.hotrod.impl.transport.tcp.TcpTransportFactory updateTopologyInfo
        [INFO] F8: INFO: ISPN004014: New server added(172.17.0.2:11222), adding to the pool.
        [INFO] F8: Nov 20, 2017 10:29:04 AM org.infinispan.client.hotrod.impl.transport.tcp.TcpTransportFactory updateTopologyInfo
        [INFO] F8: INFO: ISPN004016: Server not in cluster anymore(caching-service-app-hotrod:11222), removing from the pool.
        [INFO] F8: Nov 20, 2017 10:29:04 AM org.infinispan.client.hotrod.RemoteCacheManager start
        [INFO] F8: INFO: ISPN004021: Infinispan version: null
        [INFO] F8: Nov 20, 2017 10:29:04 AM org.infinispan.client.hotrod.impl.protocol.Codec20 readNewTopologyAndHash
        [INFO] F8: INFO: ISPN004006: 172.17.0.2:11222 sent new topology view (id=1, age=0) containing 1 addresses: [172.17.0.2:11222]
        [INFO] F8: I> No access restrictor found, access to any MBean is allowed
        [INFO] F8: Jolokia: Agent started with URL https://172.17.0.7:8778/jolokia/
        [INFO] F8: Value from Cache: 2017-11-20T10:29:04.765Z
        [INFO] F8: Value from Cache: 2017-11-20T10:29:14.870Z

After you're happy with the results, just hit ctrl-c on the console. The Fabric8 Maven Plugin will undeploy client application.
