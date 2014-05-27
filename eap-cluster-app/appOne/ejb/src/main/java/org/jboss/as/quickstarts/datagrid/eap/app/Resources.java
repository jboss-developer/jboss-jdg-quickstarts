package org.jboss.as.quickstarts.datagrid.eap.app;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.infinispan.manager.DefaultCacheManager;

public class Resources {

    @Inject
    AppOneCacheManagerProvider cacheManagerProvider;

    @Produces
    DefaultCacheManager getDefaultCacheManager() {
        return cacheManagerProvider.getCacheManager();
    }

}
