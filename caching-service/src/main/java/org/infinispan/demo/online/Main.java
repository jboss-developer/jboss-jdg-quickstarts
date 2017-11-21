package org.infinispan.demo.online;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.configuration.SaslQop;

public class Main {

   // This should match the value specified for the APPLICATION_NAME parameter when creating the caching-service
   private static final String APPLICATION_NAME = "caching-service-app";

   // Hot Rod endpoint is constructed using the following scheme: `application name`-hotrod.
   private static final String HOT_ROD_ENDPOINT_SERVICE = APPLICATION_NAME + "-hotrod";

   // This should match the value specified for the APPLICATION_USER parameter when creating the caching-service
   private static final String USERNAME = "test";

   // This should match the value specified for the APPLICATION_USER_PASSWORD parameter when creating the caching-service
   private static final String PASSWORD = "test";


   public static void main(String... args) throws InterruptedException {
      Configuration c = new ConfigurationBuilder()
            .addServer()
                .host(HOT_ROD_ENDPOINT_SERVICE)
                .security().authentication().enable()
                  .username(USERNAME)
                  .password(PASSWORD)
                  .realm("ApplicationRealm")
                  .serverName("caching-service")
                  .saslMechanism("DIGEST-MD5")
                  .saslQop(SaslQop.AUTH)
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
