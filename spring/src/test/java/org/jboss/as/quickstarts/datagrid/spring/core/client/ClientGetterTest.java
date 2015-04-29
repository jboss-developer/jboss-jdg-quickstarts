package org.jboss.as.quickstarts.datagrid.spring.core.client;

import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ClientGetterTest {

    @Test
    public void shouldGenerateRandomClients() {
        //given
        ClientGetter repository = new ClientGetter() {

            @Override
            protected void randomDelay() {
                //ignore delay
            }
        };

        //when
        List<Client> bestClients = repository.getBestClients();

        //then
        assertThat(bestClients).hasSize(10);
    }

}