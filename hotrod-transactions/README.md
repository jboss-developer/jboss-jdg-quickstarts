hotrod-transactions: Caching with Remote Transactions via Hot Rod
===========================================================================
Author: Katia Aresti
Level: Intermediate
Technologies: Infinispan, Hot Rod, Transactions
Summary: Learn how to establish a remote connection to Red Hat Data Grid using the Hot Rod protocol and then store, retrieve, and remove cache entries.
Target Product: Red Hat Data Grid
Product Versions: Red Hat Data Grid 7.3.x or later
Source: <https://github.com/infinispan/jdg-quickstart>

Hot Rod Overview
----------------

Hot Rod is a binary TCP client-server protocol designed for highly efficient interactions with Red Hat Data Grid. Hot Rod enables clients to control load balancing and failover operations as well as quickly locate data in the grid.

About This Quickstart
---------------------
This quickstart includes a *Football Manager* console application with a Hot Rod-based connector. Use the application to perform transactional cache operations that add and remove football players to teams and so on.

System Requirements
-------------------

* Java 8.0 (Java SDK 1.8) or later.
* Maven 3.0 or later. You must also [configure Maven](https://github.com/jboss-developer/jboss-developer-shared-resources/blob/master/guides/CONFIGURE_MAVEN.md#configure-maven-to-build-and-deploy-the-quickstarts).

Starting Red Hat Data Grid
-----------------------------

1. Download Data Grid server 7.3 or later from the Red Hat Customer Portal at: https://access.redhat.com/jbossnetwork/restricted/listSoftware.html

2. Open a command prompt and navigate to the root directory of your Data Grid server installation.

3. Start Data Grid.

   * Linux: `$JDG_HOME/bin/standalone.sh`
   * Windows: `%JDG_HOME%\bin\standalone.bat`

_NOTE:_ The *Football Manager* application programmatically configures a cache instance using Hot Rod administration capabilities. You do not need to create or configure caches separately.

Running the Football Manager Application
----------------------------------------
_REMINDER:_ To successfully build and run the application, you must either:
* Copy the `settings.xml` file from the root directory of this repository into your Maven installation directory.
* Pass it on the command line with `-s $QUICKSTART_HOME/settings.xml` for each `mvn` command.

  See [Build and Deploy the Quickstarts](../../README.md#build-and-deploy-the-quickstarts).

To run the *Football Manager* application, do the following:

1. Open a command prompt and navigate to the root directory of this repository.
2. Deploy the application `JAR` file.

  ```bash
  $ mvn clean package
  ```

  The command creates `target/jboss-hotrod-endpoint-quickstart.jar`.

3. Run the application.

  ```bash
  $ mvn exec:java
  ```

Performing Cache Operations via Hot Rod
---------------------------------------
When you start the *Football Manager* application, it prompts you to choose an action and displays the commands you can use to perform cache operations.

For example, to add a team, add a player to the team, and then retrieve and delete the team, do the following:

  ```bash
  $ at
  Enter a team name: Tigers

  $ ap
  Enter players name (to stop adding, type "q"): John Doe q
  Enter players country: France q

  $ p
  Tigers

  $ rt Tigers

  $ q
  ```

Type `q` again to stop the application.

Running with a Different Classpath
----------------------------------------
To run the *Football Manager* application with a classpath other than the default, do the following:

1. Compile the application.

  ```bash
  $ mvn clean package -Pcustom-classpath -Dclasspath=/custom/classpath/directory
  ```

  The command creates `target/jboss-hotrod-transactions-quickstart.jar`.

2. Run the application.

  ```bash
  $ java -jar target/jboss-hotrod-transactions-quickstart.jar
  ```

Debugging
---------
To debug source code or review Javadocs for any library in the project, run either of the following commands:

    $ mvn dependency:sources
    $ mvn dependency:resolve -Dclassifier=javadoc

These commands pull the source files into your local repository so your IDE can detect them.
