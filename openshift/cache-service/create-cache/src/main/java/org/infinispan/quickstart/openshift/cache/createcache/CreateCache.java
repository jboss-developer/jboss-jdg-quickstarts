package org.infinispan.quickstart.openshift.cache.createcache;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.commons.api.CacheContainerAdmin;

public class CreateCache {

   private static final String APP_NAME = System.getenv("APP_NAME");
   private static final String SVC_DNS_NAME = System.getenv("SVC_DNS_NAME");
   private static final String USER = "test";
   private static final String PASSWORD = "changeme";

   private static final String KEY = "hello";
   private static final String VALUE = "world";
   private static final String CACHE_NAME = "custom";

   public static void main(String[] args) throws Exception {
      final String cmd = System.getenv("CMD");

      ConfigurationBuilder cfg =
         ClientConfiguration.create(SVC_DNS_NAME, APP_NAME, USER, PASSWORD);

      switch (cmd) {
         case "create-cache":
            createCache(cfg);
            break;
         case "get-cache":
            getCache(cfg);
            break;
         case "destroy-cache":
            destroyCache(cfg);
            break;
         default:
            throw new Exception("Unknown command: " + cmd);
      }
   }

   private static void createCache(ConfigurationBuilder cfg) {
      System.out.printf("--- Connect to %s ---%n", APP_NAME);
      final RemoteCacheManager remote = new RemoteCacheManager(cfg.build());

      final String cacheName = "custom";

      System.out.printf("--- Create cache in %s ---%n", APP_NAME);

      final RemoteCache<?, ?> createdCache = remote.administration()
         .withFlags(CacheContainerAdmin.AdminFlag.PERMANENT)
         .getOrCreateCache(cacheName, "default");

      assert createdCache != null : "Expected created cache to be non-null";

      System.out.printf("--- Cache '%s' created in '%s' ---%n", cacheName, APP_NAME);
   }

   private static void getCache(ConfigurationBuilder cfg) {
      System.out.printf("--- Connect to %s ---%n", APP_NAME);
      final RemoteCacheManager remote = new RemoteCacheManager(cfg.build());

      System.out.printf("--- Store key='%s'/value='%s' pair in cache '%s'---%n", KEY, VALUE, CACHE_NAME);
      final RemoteCache<String, String> remoteCache = remote.getCache(CACHE_NAME);
      remoteCache.put(KEY, VALUE);

      System.out.printf("--- Retrieve key='%s' from cache '%s' ---%n", KEY, CACHE_NAME);
      final String value = remoteCache.get(KEY);

      System.out.printf("--- Value is '%s' ---%n", value);

      assert VALUE.equals(value) : "Expected value to be '" + value + "' but was: " + value;
   }

   private static void destroyCache(ConfigurationBuilder cfg) {
      System.out.printf("--- Connect to %s ---%n", APP_NAME);
      final RemoteCacheManager remote = new RemoteCacheManager(cfg.build());

      remote.administration()
         .withFlags(CacheContainerAdmin.AdminFlag.PERMANENT)
         .removeCache(CACHE_NAME);

      System.out.printf("--- Cache '%s' destroyed in '%s' ---%n", CACHE_NAME, APP_NAME);
   }

}
