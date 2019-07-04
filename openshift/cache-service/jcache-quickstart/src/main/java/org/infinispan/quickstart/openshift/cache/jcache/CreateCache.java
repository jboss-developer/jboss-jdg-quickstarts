package org.infinispan.quickstart.openshift.cache.jcache;

import javax.cache.*;
import javax.cache.configuration.*;
import javax.cache.spi.*;

public class CreateCache<cache> {

  public static void main(String[] args) {
    CacheManager cacheManager = Caching.getCachingProvider("org.infinispan.jcache.remote.JCachingProvider").getCacheManager();
    Cache<String, String> cache = cacheManager.createCache("default",
      new MutableConfiguration<String, String>());
  }

}
