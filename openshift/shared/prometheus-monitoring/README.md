Monitoring Red Hat Data Grid (RHDG) for OpenShift
=================================================
**Author:** Vladimir Blagojevic, Don Naro  
**Technologies:** Infinispan, Data Grid, OpenShift  
**Summary:** Learn how to integrate Data Grid with the Prometheus Operator to get advanced monitoring capabilities on OpenShift.  
**Target Product:** Data Grid for OpenShift  
**Product Versions:** RHDG 7.3 or later

About This Quickstart
---------------------
This quickstart demonstrates how you can monitor Data Grid for OpenShift with Prometheus and Grafana.

**NOTE:** Data Grid for OpenShift supports Prometheus monitoring with both `cache-service` and `datagrid-service`. However, this quickstart is designed to work with a `datagrid-service` pod in a specific namespace.

Setting Up Administrator Privileges
------------------------------------
This quickstart requires administration access for the OpenShift cluster to deploy the Prometheus Operator.

1. Enable the `admin-user` addon for Minishift, if necessary.
```bash
$ minishift addon enable admin-user
```

2. Log in as the system administrator.
```bash
$ oc login -u system:admin
```

3. Add the `cluster-admin` role to the the developer user.
```bash
$ oc adm policy add-cluster-role-to-user cluster-admin developer
```

Deploying a Data Grid Service
-----------------------------

1. Create a `demo-monitoring` project.
```bash
$ oc new-project demo-monitoring
```

2. Ensure a _registry.redhat.io_ pull secret is in the project and create one if necessary.
```bash
$ oc get secrets
```

3. Deploy `datagrid-service` with monitoring enabled.
```bash
$ oc new-app datagrid-service \
  -p APPLICATION_USER=${user} \
  -p APPLICATION_PASSWORD=${password} \
  -p APPLICATION_NAME=${appName} \
  -e AB_PROMETHEUS_ENABLE=true
```
  See the [Red Hat Data Grid for OpenShift](https://access.redhat.com/documentation/en-us/red_hat_data_grid/7.3/html-single/data_grid_for_openshift/) documentation for more information.

Installing the Prometheus Operator
----------------------------------
Run the script to deploy the Prometheus Operator and assign the required roles, bindings, and service accounts.

```bash
$ ./install-prometheus.sh
```

Integrating Data Grid with Prometheus
-------------------------------------

1. Create a route that exposes `datagrid-service` internally.
```bash
$ oc expose svc/datagrid-service
```

2. Create a Data Grid metrics service.
```bash
$ oc apply -f service-metrics.yaml
```

3. Create a route that exposes the Data Grid metrics service internally.
```bash
$ oc expose svc/datagrid-app-metrics
```

4. Create a service monitor lets Prometheus connect to the Data Grid metrics service.
```bash
$ oc apply -f service-monitor.yaml
```

Opening the Prometheus Console
------------------------------

1. Get the hostname for the Prometheus pod.
```bash
$ oc get route prometheus
NAME         HOST/PORT
prometheus   prometheus-demo-monitoring.192.0.2.0.nip.io
```

2. Navigate to the hostname in your browser. Note that the route uses HTTPS, for example: `https://prometheus-demo-monitoring.192.0.2.0.nip.io`

Setting Up Monitoring with Grafana
----------------------------------

1. Run the script to deploy Grafana and use Prometheus as a datasource for metrics.
```bash
$ ./install-grafana.sh  
```

2. Get the hostname for the Grafana pod.
```bash
$ oc get route grafana
NAME      HOST/PORT
grafana   grafana-demo-monitoring.192.0.2.0.nip.io
```

3. Navigate to the hostname in your browser.

4. Log in to Grafana with the following credentials:
  - _username:_ admin
  - _password:_ admin

5. If prompted, specify a new password to continue login.

6. Create a Data Grid dashboard as follows:

  a. Navigate to the **Import** page, for example:
  `http://grafana-demo-monitoring.192.0.2.0.nip.io/dashboard/import`

  b. Select **Upload .json file** and then select `grafana-dashboard.json` from this quickstart directory.

  c. Select **Prometheus** as the data source and then select **Import**.

  The Data Grid dashboard opens in your browser where you can monitor your OpenShift cluster.

Cleaning Up Resources
---------------------
Now that you've successfully completed this tutorial, you should remove resources as well as cluster administration privileges.

1. Delete the `demo-monitoring` project.
```bash
$ oc delete project demo-monitoring
```

2. Log in as the system administrator.
```bash
$ oc login -u system:admin
```

3. Remove the `cluster-admin` role for the developer user.
```bash
$ oc adm policy remove-cluster-role-from-user cluster-admin developer
```

4. Log out as the system administrator.
```
$ oc logout
```
