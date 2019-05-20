Embedding Red Hat Data Grid on OpenShift
==================================================================
Author: Katia Aresti
Level: Intermediate
Technologies: Infinispan, OpenShift
Summary: Learn how to deploy an application on OpenShift that embeds Red Hat Data Grid.
Target Product: Red Hat Data Grid
Product Versions: Red Hat Data Grid 7.3

About This Quickstart
-----------
This quickstart includes a containerized application that demonstrates how to use Red Hat Data Grid in Library mode on OpenShift.

System Requirements
-------------------

* Java 8.0 (Java SDK 1.8) or later.
* Maven 3.0 or later. You must also [configure Maven](https://github.com/jboss-developer/jboss-developer-shared-resources/blob/master/guides/CONFIGURE_MAVEN.md#configure-maven-to-build-and-deploy-the-quickstarts).
* OpenShift 3 client.
* A local OpenShift installation.
* A version of Docker that is compatible with your local OpenShift installation.

Setting Up the OpenShift Environment
------------------------------------
To set up your OpenShift environment, do the following:

1. Start the Docker daemon if it is not already running.
2. Start your local OpenShift cluster if it is not already running.
3. Log in as the developer user.

  ```bash
  $ oc login -u developer -p developer
  ```

4. Ensure the `myproject` namespace is available and switch to it if necessary. This quickstart is configured to use the `myproject` namespace. If you use a different project, you must specify the namespace with the `fabric8.namespace` parameter in `pom.xml`.

5. Add the view role to the default service account. The [KUBE_PING](https://github.com/jgroups-extras/jgroups-kubernetes) discovery protocol must have view access for all pods in your project namespace.

  ```bash
  $ oc policy add-role-to-user view system:serviceaccount:$(oc project -q):default -n $(oc project -q)
  ```

Building and Deploying the Quickstart Application
-------------------------------------------------
To build the quickstart application, do the following:

1. Open a command prompt and navigate to the root directory of this repository.
2. Build the application `JAR` file.

  ```bash
  $ mvn clean package -P docker
  ```

  This command creates `target/jboss-embedded-openshift-quickstart.jar`. The `target/` directory also contains a Dockerfile and other deployment artifacts. The `target/classes/META-INF/fabric8` directory contains Kubernetes and OpenShift deployment templates.

3. Deploy the quickstart application to OpenShift.

  ```bash
  $ mvn fabric8:run -P docker
  ```

  This command uses the OpenShift source-to-image build process to create an image stream, deployment configuration, and pod named `jboss-embedded-openshift-quickstart`.

Working with the Quickstart Application
---------------------------------------
After you build and deploy the quickstart application, do the following to evaluate it:

1. Get the status for the quickstart application.

  ```bash
  $ oc status
  ```

2. Scale the quickstart application up or down.

  ```bash
  $ oc scale dc jboss-embedded-openshift-quickstart --replicas=<number>
  ```
