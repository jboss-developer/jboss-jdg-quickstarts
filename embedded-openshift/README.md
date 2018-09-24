Example of using Embedded Datagrid on OpenShift
==================================================================
Author: Katia Aresti
Level: Intermediate
Technologies: Infinispan, OpenShift
Summary: The `embedded-openshift` quickstart demonstrates how to deploy an application that uses datagrid in
embedded mode
Target Product: JDG
Product Versions: JDG 7.3

What is it?
-----------

The `embedded-openshift` quickstart demonstrates how to use and deploy applications using datagrid in embedded mode.

Prerequisites
-------------

In order to run this project one needs to have an access to local OpenShift installation.

You can use local OpenShift all-in-one cluster. To prepare you local OpenShift cluster follow [the documentation](https://github.com/openshift/origin/blob/master/docs/cluster_up_down.md).

Maven and [Docker](https://www.docker.com/) daemon running in the background.

System requirements
-------------------

All you need to build this project is Java 8.0 (Java SDK 1.8) or better, Maven 3.0 or better and OpenShift 3 client.

Configure Maven
---------------

If you have not yet done so, you must [Configure Maven](https://github.com/jboss-developer/jboss-developer-shared-resources/blob/master/guides/CONFIGURE_MAVEN.md#configure-maven-to-build-and-deploy-the-quickstarts) before testing the quickstarts.

Configuring Service Account for KUBE_PING
-----------------------------------------
Since https://github.com/jgroups-extras/jgroups-kubernetes[KUBE_PING] Discovery Protocol requires viewing all Pods in the OpenShift Project, you need to add additional privileges to the OpenShift:


      oc cluster up
      oc login -u system:admin
      oc policy add-role-to-user view system:serviceaccount:myproject:default -n myproject


Switch to a normal user
-----------------------
After the OpenShift is running, you need to login as a `developer`. That's the standard role you should always be using:

      oc login -u developer -p developer

Build the Quickstart
--------------------

Type this command to build the quickstart:

      mvn clean package

Note that `target/` directory contains additional directories like `docker` (with generated Dockerfile) and `classes/META-INF/fabric8` with Kubernetes and OpenShift deployment templates.

TIP: If the Docker Daemon is down, the build will omit processing Dockerfiles. Use `docker` profile to turn it on manually.

Deploying this tutorial in OpenShift
------------------------------------
This is handles automatically by Fabric8 maven plugin, just invoke:

        mvn fabric8:run

Viewing and scaling
-------------------
Everything should be up and running at this point. Now login into the [OpenShift web console](https://127.0.0.1:8443/) and scale the application up or down.