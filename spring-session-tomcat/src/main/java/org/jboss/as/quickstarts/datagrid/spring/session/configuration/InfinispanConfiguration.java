package org.jboss.as.quickstarts.datagrid.spring.session.configuration;

import org.infinispan.spring.provider.SpringEmbeddedCacheManagerFactoryBean;
import org.infinispan.spring.session.configuration.EnableInfinispanEmbeddedHttpSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableInfinispanEmbeddedHttpSession
public class InfinispanConfiguration {

   @Bean
   public SpringEmbeddedCacheManagerFactoryBean springCache() {
      return new SpringEmbeddedCacheManagerFactoryBean();
   }
   
}
