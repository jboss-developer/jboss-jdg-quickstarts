package org.jboss.as.quickstarts.datagrid.spring.rest.configuration;

import org.jboss.as.quickstarts.datagrid.spring.core.client.ClientCache;
import org.jboss.as.quickstarts.datagrid.spring.core.client.CachedClientGetter;
import org.jboss.as.quickstarts.datagrid.spring.core.client.ClientGetter;
import org.jboss.as.quickstarts.datagrid.spring.rest.CacheRest;
import org.jboss.as.quickstarts.datagrid.spring.rest.ClientsRest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * REST Services configuration
 *
 * @author Sebastian Laskawiec
 */
@Configuration
public class RestConfiguration {

    @Bean
    public ClientsRest cachedClientRest(@Qualifier("cachedClientGetter") CachedClientGetter cachedClientGetter, @Qualifier("clientGetter") ClientGetter clientGetter) {
        return new ClientsRest(cachedClientGetter, clientGetter);
    }

    @Bean
    public CacheRest cacheRest(ClientCache clientCache) {
        return new CacheRest(clientCache);
    }

}
