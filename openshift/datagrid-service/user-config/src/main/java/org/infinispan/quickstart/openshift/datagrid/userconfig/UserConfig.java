package org.infinispan.quickstart.openshift.datagrid.userconfig;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

public class UserConfig {

   private static final String APP_NAME = System.getenv("APP_NAME");

   private static final String KEY = "key-hotrod";
   private static final String VALUE = "user-config";

   public static void main(String[] args) throws Exception {
      ConfigurationBuilder cfg =
         ClientConfiguration.create(APP_NAME);

      System.out.printf("--- Connect to %s ---%n", APP_NAME);
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
