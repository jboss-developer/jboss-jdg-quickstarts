package org.jboss.as.quickstarts.datagrid.spring.core.client;

import org.jboss.as.quickstarts.datagrid.spring.bootstrap.Application;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class ClientCacheTest {

    @Autowired
    private ClientCache clientCache;

    @Autowired
    private CachedClientGetter cachedClientGetter;

    @Before
    public void before() {
        clientCache.clearCache();
    }

    @Test
    public void shouldReturnClientFromCache() throws Exception {
        //given
        cachedClientGetter.getBestClients();

        //when
        Collection<Client> cachedClients = clientCache.getCachedClients();

        //then
        assertThat(cachedClients).hasSize(10);
    }

    @Test
    public void shouldReturnEmptyCollectionWhen() throws Exception {
        //when
        Collection<Client> cachedClients = clientCache.getCachedClients();

        //then
        assertThat(cachedClients).isEmpty();
    }

}