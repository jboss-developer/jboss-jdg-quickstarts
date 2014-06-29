hotrod-secured: Use JDG remotely through Hotrod with Secured authentication/authorization
=========================================================================================
Author: Tristan Tarrant, Martin Gencur, Vitalii Chepeliuk
Level: Intermediate
Technologies: Infinispan, Hot Rod
Summary: Demonstrates how to use Infinispan remotely using the Hot Rod protocol and secured authentication/authorization.
Target Product: JDG
Product Versions: JDG 6.3
Source: <https://github.com/jboss-developer/jboss-jdg-quickstarts>

What is it?
-----------

Hot Rod is a binary TCP client-server protocol used in JBoss Data Grid. The Hot Rod protocol facilitates faster client and server interactions in comparison to other text based protocols(Memcached, REST) and allows clients to make decisions about load balancing, failover and data location operations.

This quickstart demonstrates how to connect securely to remote JBoss Data Grid (JDG) to store, retrieve, and remove data from cache using the Hot Rod protocol. It is a simple Football Manager console application allows:
  coach can add or remove players from teams, or print a list of the all players
  player to see information about other players
  in the team using the Hot Rod based connector.

System requirements
-------------------

All you need to build this project is Java 6.0 (Java SDK 1.6) or higher, Maven 3.0 or higher.
The application this project produces is designed to be run on JBoss Data Grid 6.3

Configure Maven
---------------

If you have not yet done so, you must [Configure Maven](../../README.md#configure-maven) before testing the quickstarts.

Hot Rod authentication
----------------------
The Hot Rod protocol supports authentication since version 2.0 (Infinispan 7.0) by leveraging the SASL mechanisms. The supported SASL mechanisms (usually shortened as mechs) are:

* PLAIN - This is the most insecure mech, since credentials are sent over the wire in plain-text format, however it is the simplest to get to work. In combination with encryption (i.e. SSL) it can be used safely
* DIGEST-MD5 - This mech hashes the credentials before sending them over the wire, so it is more secure than PLAIN
* GSSAPI - This mech uses Kerberos tickets, and therefore requires the presence of a properly configured Kerberos Domain Controller (such as Microsoft Active Directory)
* EXTERNAL - This mech obtains credentials from the underlying transport (i.e. from a X.509 client certificate) and therefore requires encryption using client-certificates to be enabled.

Configure JDG
-------------

1. Obtain JDG server distribution on Red Hat's Customer Portal at https://access.redhat.com/jbossnetwork/restricted/listSoftware.html

2. This Quickstart uses new HotRod Security features to configure the cache. To use it, it's necessary to change JDG configuration file (`JDG_HOME/standalone/configuration/standalone.xml`) to contain the following definitions:
* Security Realm configuration
  Security Realms are used by the server to provide authentication and authorization information for both the management and application interfaces

       <management>
            ...
            <security-realm name="ApplicationRealm">
               <authentication>
                  <properties path="application-users.properties" relative-to="jboss.server.config.dir"/>
               </authentication>
               <authorization>
                  <properties path="application-roles.properties" relative-to="jboss.server.config.dir"/>
               </authorization>
            </security-realm>
            ...
        </management>
   
* Enpoint subsystem definition:
  The following configuration enables authentication against ApplicationRealm, using the DIGEST-MD5 SASL mechanism: 
    
		    <subsystem xmlns="urn:infinispan:server:endpoint:6.1">
			    <hotrod-connector socket-binding="hotrod" cache-container="local">
				    <topology-state-transfer lazy-retrieval="false" lock-timeout="1000" replication-timeout="5000"/>
					    <authentication security-realm="ApplicationRealm">
					    <sasl server-name="football" mechanisms="DIGEST-MD5" qop="auth">
						    <policy>
							    <no-anonymous value="true"/>
						    </policy>
					    <property name="com.sun.security.sasl.digest.utf8">true</property>
					    </sasl>
				    </authentication>
			    </hotrod-connector>
                ...
		    </subsystem>
  Notice! The server-name attribute: it is the name that the server declares to incoming clients and therefore the client configuration must match.

* Infinispan subsystem definition:
  Server supports authorization with cache configuration defined below

		    <subsystem xmlns="urn:infinispan:server:core:6.1">
			    <cache-container name="local" default-cache="teams">
				    <security>
					    <authorization>
					        <identity-role-mapper/>
					        <role name="coach" permissions="READ WRITE"/>
					        <role name="player" permissions="READ"/>
					    </authorization>
				    </security>
				    <local-cache name="teams" start="EAGER" batching="false">
					    <transaction mode="NONE"/>
					    <security>
						    <authorization roles="coach player"/>
					    </security>
				    </local-cache>
                ...
		    </subsystem>

Start JDG
---------

1. Open a command line and navigate to the root of the JDG directory.
2. The following shows the command line to start the server with the web profile:

        For Linux:   $JDG_HOME/bin/standalone.sh
        For Windows: %JDG_HOME%\bin\standalone.bat

Add new users to ApplicationRealm
---------------------------------
<JDG_HOME>/bin/add-user.sh -a -u 'coach'      -p 'qwerty110!' -ro coach
<JDG_HOME>/bin/add-user.sh -a -u 'player'     -p 'qwerty111!' -ro player

Hot Rod client configuration
----------------------------
  Once you have configured a secured Hot Rod connector, you can connect to it using the Hot Rod client:
  
    public class LoginHandler implements CallbackHandler {
        final private String login;
        final private char[] password;
        final private String realm;

        public LoginHandler(String login, char[] password, String realm) {
            this.login = login;
            this.password = password;
            this.realm = realm;
        }

        @Override
        public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
            for (Callback callback : callbacks) {
                if (callback instanceof NameCallback) {
                    ((NameCallback) callback).setName(login);
                } else if (callback instanceof PasswordCallback) {
                    ((PasswordCallback) callback).setPassword(password);
                } else if (callback instanceof RealmCallback) {
                    ((RealmCallback) callback).setText(realm);
                } else {
                    throw new UnsupportedCallbackException(callback);
                }
            }
        }
    }

    ConfigurationBuilder clientBuilder = new ConfigurationBuilder();
    clientBuilder
        .addServer()
            .host("127.0.0.1")
            .port(11222)
        .socketTimeout(1200000)
        .security()
            .authentication()
                .enable()
                .serverName("football")
                .saslMechanism("DIGEST-MD5")
                .callbackHandler(new LoginHandler("coach", "qwerty110!".toCharArray(), "ApplicationRealm"));
    remoteCacheManager = new RemoteCacheManager(clientBuilder.build());
    RemoteCache<String, String> cache = remoteCacheManager.getCache("teams");

Build and Run the Quickstart
----------------------------

_NOTE: The following build command assumes you have configured your Maven user settings. If you have not, you must include Maven setting arguments on the command line. See [Build and Deploy the Quickstarts](../../README.md#build-and-deploy-the-quickstarts) for complete instructions and additional options._

1. Make sure you have started the JDG as described above.
2. Open a command line and navigate to the root directory of this quickstart.
3. Type this command to build and deploy the archive:

        mvn clean package 
                
4. This will create a file at `target/jboss-hotrod-secured-quickstart.jar`

5. Run the example application in its directory:

        mvn exec:java 

Using the application
---------------------
Basic usage scenarios can look like this (keyboard shortcuts will be shown to you upon 

        at  -  add a team
        ap  -  add a player to a team
        rt  -  remove a team
        rp  -  remove a player from a team
        p   -  print all teams and players
        q   -  quit
        
Type `q` one more time to exit the application.

Run application with different classpath
----------------------------------------
It's possible to run this quickstart with different classpath (other than default created by mvn exec:java),
for instance with ${infinispan-server}/client/hotrod/java classpath.
To do this, compile quickstart with:

        mvn clean package -Pcustom-classpath -Dclasspath=/custom/classpath

This will create a file at `target/jboss-hotrod-secured-quickstart.jar`.
Then you can run it with:

        java -jar target/jboss-hotrod-secured-quickstart.jar

Debug the Application
------------------------------------

If you want to debug the source code or look at the Javadocs of any library in the project, run either of the following commands to pull them into your local repository. The IDE should then detect them.

    mvn dependency:sources
    mvn dependency:resolve -Dclassifier=javadoc
