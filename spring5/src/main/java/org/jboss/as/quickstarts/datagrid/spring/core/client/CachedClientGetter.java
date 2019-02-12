package org.jboss.as.quickstarts.datagrid.spring.core.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Extremely slow Clients repository.
 *
 * @author Sebastian Laskawiec
 */
public class CachedClientGetter extends ClientGetter {

    private final ClientGetter clientGetter;

    public CachedClientGetter(ClientGetter clientGetter) {
        this.clientGetter = clientGetter;
    }

    @Cacheable(value = "clients")
    public List<Client> getBestClients() {
        return super.getBestClients();
    }

}
