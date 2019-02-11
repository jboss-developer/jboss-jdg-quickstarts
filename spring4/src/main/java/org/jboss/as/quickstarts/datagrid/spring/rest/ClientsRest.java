package org.jboss.as.quickstarts.datagrid.spring.rest;

import org.jboss.as.quickstarts.datagrid.spring.core.client.CachedClientGetter;
import org.jboss.as.quickstarts.datagrid.spring.core.client.Client;
import org.jboss.as.quickstarts.datagrid.spring.core.client.ClientGetter;
import org.jboss.as.quickstarts.datagrid.spring.rest.model.Clients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.invoke.MethodHandles;
import java.util.List;

/**
 * REST Cache endpoint.
 *
 * @author Sebastian Laskawiec
 */
@RequestMapping("clients")
public class ClientsRest {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final CachedClientGetter cachedClientGetter;

    private final ClientGetter clientGetter;

    public ClientsRest(CachedClientGetter cachedClientGetter, ClientGetter clientGetter) {
        this.cachedClientGetter = cachedClientGetter;
        this.clientGetter = clientGetter;
    }

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody
    Clients getClients(@RequestParam(value = "caching", defaultValue = "false") boolean caching) {
        long startTime = System.currentTimeMillis();

        List<Client> clients;
        if(caching) {
            clients = cachedClientGetter.getBestClients();
        } else {
            clients = clientGetter.getBestClients();
        }

        logger.info("Loading Clients took {} ms", System.currentTimeMillis() - startTime);
        return new Clients(clients);
    }
}
