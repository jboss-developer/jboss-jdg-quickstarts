package org.jboss.as.quickstarts.datagrid.spring.core.configuration;

import org.infinispan.spring.embedded.provider.SpringEmbeddedCacheManagerFactoryBean;
import org.jboss.as.quickstarts.datagrid.spring.core.client.ClientCache;
import org.jboss.as.quickstarts.datagrid.spring.core.client.CachedClientGetter;
import org.jboss.as.quickstarts.datagrid.spring.core.client.ClientGetter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for domain objects.
 *
 * @author Sebastian Laskawiec
 */
@Configuration
@EnableCaching
@EnableAutoConfiguration
public class DomainConfig {

    @Bean
    public SpringEmbeddedCacheManagerFactoryBean springCache() {
        return new SpringEmbeddedCacheManagerFactoryBean();
    }

    @Bean
    public ClientGetter clientGetter() {
        return new ClientGetter();
    }

    @Bean
    public CachedClientGetter cachedClientGetter(ClientGetter clientGetter) {
        return new CachedClientGetter(clientGetter);
    }

    @Bean
    public ClientCache cacheHandler(CacheManager cacheManager) {
        return new ClientCache(cacheManager);
    }
}
