package org.infinispan.quickstart.openshift.shared.external;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

public class ExternalAccess {

   private static final String USER = "test";
   private static final String PASSWORD = "changeme";

   private static final String KEY = "external";
   private static final String VALUE = "access";

   public static void main(String[] args) {
      String appName = args[0];

      ConfigurationBuilder cfg =
         ClientConfiguration.create(appName, USER, PASSWORD);

      System.out.printf("--- Connect to %s ---%n", appName);
      final RemoteCacheManager remote = new RemoteCacheManager(cfg.build());

      System.out.printf("--- Store key='%s'/value='%s' pair ---%n", KEY, VALUE);
      final RemoteCache<String, String> remoteCache = remote.getCache();
      remoteCache.clear();
      remoteCache.put(KEY, VALUE);

      System.out.printf("--- Retrieve key='%s' ---%n", KEY);
      final String value = remoteCache.get(KEY);

      System.out.printf("--- Value is '%s' ---%n", value);

      assert VALUE.equals(value) : "Expected value to be '" + value + "' but was: " + value;

      remote.stop();
   }

}
