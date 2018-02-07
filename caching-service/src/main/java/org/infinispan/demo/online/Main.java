package org.infinispan.demo.online;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.configuration.SaslQop;

public class Main {

   // This is where the openshift CRT file is located, DO NOT CHANGE
   private static final String CRT_PATH = "/var/run/secrets/kubernetes.io/serviceaccount/service-ca.crt";

   // This should match the value specified for the APPLICATION_NAME parameter when creating the caching-service
   private static final String APPLICATION_NAME = "caching-service-app";

   // Hot Rod endpoint is constructed using the following scheme: `application name`-hotrod.
   private static final String HOT_ROD_ENDPOINT_SERVICE = APPLICATION_NAME + "-hotrod";

   // This should match the value specified for the APPLICATION_USER parameter when creating the caching-service
   private static final String USERNAME = "test";

   // This should match the value specified for the APPLICATION_USER_PASSWORD parameter when creating the caching-service
   private static final String PASSWORD = "test";

   // This is the password for the trustore which will be created
   private static final char[] TRUSTSTORE_PASSWORD = "secret".toCharArray();

   // This is the path of the generated truststore, here we simply use the home dir on the deployed openshift pod
   private static final String TRUSTSTORE_PATH = "truststore.jks";

   public static void main(String... args) throws GeneralSecurityException, InterruptedException, IOException {

      TrustStore.createFromCrtFile(CRT_PATH, TRUSTSTORE_PATH, TRUSTSTORE_PASSWORD);

      Configuration c = new ConfigurationBuilder()
            .addServer()
                .host(HOT_ROD_ENDPOINT_SERVICE)
                .port(11222)
               .security()
                .authentication().enable()
                  .username(USERNAME)
                  .password(PASSWORD)
                  .realm("ApplicationRealm")
                  .serverName("caching-service")
                  .saslMechanism("DIGEST-MD5")
                  .saslQop(SaslQop.AUTH)
                .ssl().enable()
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
