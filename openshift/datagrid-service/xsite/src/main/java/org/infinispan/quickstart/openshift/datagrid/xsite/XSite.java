package org.infinispan.quickstart.openshift.datagrid.xsite;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

public class XSite {

   private static final String SVC_DNS_NAME = System.getenv("SVC_DNS_NAME");
   private static final String CMD = System.getenv("CMD");

   private static final String KEY = "cross";
   private static final String VALUE = "site";

   private static final String CACHE_NAME = "cross-site";

   public static void main(String[] args) throws Exception {
      ConfigurationBuilder cfg =
         ClientConfiguration.create(SVC_DNS_NAME);

      System.out.printf("--- Connect to %s ---%n", SVC_DNS_NAME);
      final RemoteCacheManager remote = new RemoteCacheManager(cfg.build());

      switch (CMD) {
         case "put-data":
            putData(remote);
            break;
         case "get-data":
            getData(remote);
            break;
         default:
            throw new Exception("Unknown command: " + CMD);
      }
   }

   private static void getData(RemoteCacheManager remote) {
      System.out.printf("--- Retrieve key='%s' ---%n", KEY);
      final RemoteCache<String, String> remoteCache = remote.getCache(CACHE_NAME);
      final String value = remoteCache.get(KEY);

      System.out.printf("--- Value is '%s' ---%n", value);

      assert VALUE.equals(value) : "Expected value to be '" + VALUE + "' but was: " + value;
   }

   private static void putData(RemoteCacheManager remote) {
      System.out.printf("--- Store key='%s'/value='%s' pair ---%n", KEY, VALUE);
      final RemoteCache<String, String> remoteCache = remote.getCache(CACHE_NAME);
      remoteCache.put(KEY, VALUE);
   }

}
