package org.infinispan.quickstart.openshift.datagrid.userconfig;

import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

class ClientConfiguration {

   private ClientConfiguration() {
   }

   static ConfigurationBuilder create(String svcName) {
      final ConfigurationBuilder cfg = new ConfigurationBuilder();

      cfg
         .addServer()
            .host(svcName)
            .port(11222);

      return cfg;
   }

}
