package org.infinispan.demo.online;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ClientIntelligence;
import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.configuration.SaslQop;

/**
 * DEPRECATED. Use the OpenShift quickstarts for Data Grid 7.3 instead.
 */

/**
 * An openshift client which is executed locally to connect to an external openshift instance
 */
public class ExternalOpenShiftClient {

   // Hot Rod endpoint is the URL which is displayed for the hotrod route you created (do not include https://)
   private static final String HOT_ROD_ENDPOINT_ROUTE = "example-route.com";

   // This should match the value specified for the APPLICATION_USER parameter when creating the caching-service
   private static final String USERNAME = "test";

   // This should match the value specified for the APPLICATION_USER_PASSWORD parameter when creating the caching-service
   private static final String PASSWORD = "test";

   // This is the password for the trustore which will be created
   private static final char[] TRUSTSTORE_PASSWORD = "secret".toCharArray();

   // This is the path of the generated truststore, here we simply use the home dir on the deployed openshift pod
   private static final String TRUSTSTORE_PATH = "truststore.pkcs12";

   public static void main(String... args) throws GeneralSecurityException, InterruptedException, IOException {

      TrustStore.createFromCmdLine(TRUSTSTORE_PATH, TRUSTSTORE_PASSWORD);

      Configuration c = new ConfigurationBuilder()
            .addServer()
                .host(HOT_ROD_ENDPOINT_ROUTE)
                .port(443)
               .clientIntelligence(ClientIntelligence.BASIC)
               .security()
                .authentication().enable()
                  .username(USERNAME)
                  .password(PASSWORD)
                  .realm("ApplicationRealm")
                  .serverName("caching-service")
                  .saslMechanism("DIGEST-MD5")
                  .saslQop(SaslQop.AUTH)
                .ssl().enable()
                  .sniHostName(HOT_ROD_ENDPOINT_ROUTE)
                  .trustStoreFileName(TRUSTSTORE_PATH)
                  .trustStorePassword(TRUSTSTORE_PASSWORD)
            .build();

      // When using topology and hash aware client (this is the default), the client
      // obtains a list of cluster members during PING operation. Next, the client
      // initialized P2P connection to each cluster members to reach data
      // in a single network hop.
      RemoteCacheManager remoteCacheManager = new RemoteCacheManager(c);

      // Caching Service uses only one, default cache.
      RemoteCache<String, String> cache = remoteCacheManager.getCache();

      // From this point we can utilize Hot Rod client the way we want...
      while (true) {
         cache.put("test", Instant.now().toString());
         System.out.println("Value from Cache: " + cache.get("test"));
         TimeUnit.SECONDS.sleep(10);
      }
   }
}
