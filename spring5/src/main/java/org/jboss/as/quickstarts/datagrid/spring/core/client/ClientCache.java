package org.jboss.as.quickstarts.datagrid.spring.core.client;

import org.infinispan.AdvancedCache;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Handles direct cache access.
 *
 * @author Sebastian Laskawiec
 */
public class ClientCache {

    private Cache cache;
    private AdvancedCache<String, Collection<Client>> nativeCache;

    public ClientCache(CacheManager cacheManager) {
        this.cache = cacheManager.getCache("clients");
        this.nativeCache = (AdvancedCache<String, Collection<Client>>) cache.getNativeCache();
    }

    public void clearCache() {
        cache.clear();
    }

    public Collection<Client> getCachedClients() {
        //since we stored a list of client - in reality it maps for a single key in infinispan and list of clients
        //as values corresponding to that key.
        Set<String> keys = nativeCache.keySet();
        if(keys.size() > 0) {
            return nativeCache.get(keys.iterator().next());
        }
        return Collections.emptyList();
    }

}
