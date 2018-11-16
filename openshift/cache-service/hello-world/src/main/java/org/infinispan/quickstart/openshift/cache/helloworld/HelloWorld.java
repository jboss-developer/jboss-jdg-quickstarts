package org.infinispan.quickstart.openshift.cache.helloworld;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

public class HelloWorld {

   private static final String SERVICE_NAME = "cache-service";
   private static final String HOST = SERVICE_NAME + "-hotrod";
   private static final String USER = "test";
   private static final String PASSWORD = "changeme";

   private static final String KEY = "hello";
   private static final String VALUE = "world";

   public static void main(String[] args) {
      ConfigurationBuilder cfg =
         ClientConfiguration.create(HOST, SERVICE_NAME, USER, PASSWORD);

      System.out.printf("--- Connect to %s ---%n", SERVICE_NAME);
      final RemoteCacheManager remote = new RemoteCacheManager(cfg.build());

      System.out.printf("--- Store key='%s'/value='%s' pair ---%n", KEY, VALUE);
      final RemoteCache<String, String> remoteCache = remote.getCache();
      remoteCache.put(KEY, VALUE);

      System.out.printf("--- Retrieve key='%s' ---%n", KEY);
      final String value = remoteCache.get(KEY);

      System.out.printf("--- Value is '%s' ---%n", value);

      assert VALUE.equals(value) : "Expected value to be '" + value + "' but was: " + value;
   }

}
