package org.jboss.as.quickstarts.datagrid.spring.rest;

import org.jboss.as.quickstarts.datagrid.spring.core.client.ClientCache;
import org.jboss.as.quickstarts.datagrid.spring.rest.model.Clients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.lang.invoke.MethodHandles;

/**
 * REST client based on Cached Client Getter.
 *
 * @author Sebastian Laskawiec
 */
@RequestMapping("cache")
public class CacheRest {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ClientCache clientCache;

    public CacheRest(ClientCache clientCache) {
        this.clientCache = clientCache;
    }

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody
    Clients getValuesFromCache() {
        return new Clients(clientCache.getCachedClients());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/clear")
    @ResponseStatus(value = HttpStatus.OK)
    public void clearCache() {
        clientCache.clearCache();
    }
}
