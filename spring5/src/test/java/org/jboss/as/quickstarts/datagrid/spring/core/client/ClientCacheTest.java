package org.jboss.as.quickstarts.datagrid.spring.core.client;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;

import org.jboss.as.quickstarts.datagrid.spring.core.configuration.DomainConfig;
import org.jboss.as.quickstarts.datagrid.spring.rest.configuration.RestConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {DomainConfig.class, RestConfiguration.class})
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
    public void shouldReturnClientFromCache() {
        //given
        cachedClientGetter.getBestClients();

        //when
        Collection<Client> cachedClients = clientCache.getCachedClients();

        //then
        assertThat(cachedClients).hasSize(10);
    }

    @Test
    public void shouldReturnEmptyCollectionWhen() {
        //when
        Collection<Client> cachedClients = clientCache.getCachedClients();

        //then
        assertThat(cachedClients).isEmpty();
    }

}