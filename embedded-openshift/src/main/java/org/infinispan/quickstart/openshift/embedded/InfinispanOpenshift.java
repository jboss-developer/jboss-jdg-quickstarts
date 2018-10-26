package org.infinispan.quickstart.openshift.embedded;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;

public class InfinispanOpenshift {

   public static void main(String[] args) throws UnknownHostException {
      //Configure Infinispan to use default transport and the default Kubernetes JGroups configuration.
      GlobalConfiguration globalConfig = new GlobalConfigurationBuilder().transport()
            .defaultTransport()
            .addProperty("configurationFile", "default-configs/default-jgroups-kubernetes.xml")
            .build();

      // Use a distributed cache for the quickstart application.
      Configuration cacheConfiguration = new ConfigurationBuilder()
            .clustering()
            .cacheMode(CacheMode.REPL_SYNC)
            .build();

      DefaultCacheManager cacheManager = new DefaultCacheManager(globalConfig, cacheConfiguration);
      cacheManager.defineConfiguration("default", cacheConfiguration);
      Cache<String, String> cache = cacheManager.getCache("default");

      //Each cluster member updates its own entry in the cache.
      String hostname = Inet4Address.getLocalHost().getHostName();
      ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
      scheduler.scheduleAtFixedRate(() -> {
               String time = Instant.now().toString();
               cache.put(hostname, time);
               System.out.println("[" + time + "][" + hostname + "] Values from the cache: ");
               cache.entrySet().forEach(entry ->
                     System.out.printf("Host %s Time %s \n", entry.getKey(), entry.getValue())
               );
            },
            0, 2, TimeUnit.SECONDS);

      try {
         //The container operates for one hour and then shuts down.
         TimeUnit.HOURS.sleep(1);
      } catch (InterruptedException e) {
         scheduler.shutdown();
         cacheManager.stop();
      }
   }
}
