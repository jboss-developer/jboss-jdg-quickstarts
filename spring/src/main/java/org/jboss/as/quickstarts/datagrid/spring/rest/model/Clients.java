package org.jboss.as.quickstarts.datagrid.spring.rest.model;

import org.jboss.as.quickstarts.datagrid.spring.core.client.Client;

import java.util.Collection;

/**
 * Model for REST interface. Created to meet Ember JS needs.
 *
 * @author Sebastian Laskawiec
 */
public class Clients {

    private Collection<Client> clients;

    public Collection<Client> getClients() {
        return clients;
    }

    public void setClients(Collection<Client> clients) {
        this.clients = clients;
    }

    public Clients(Collection<Client> clients) {
        this.clients = clients;
    }
}
