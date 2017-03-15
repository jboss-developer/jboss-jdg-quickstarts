package org.jboss.as.quickstarts.datagrid.spring.session.configuration;

import org.infinispan.spring.provider.SpringEmbeddedCacheManagerFactoryBean;
import org.infinispan.spring.session.configuration.EnableInfinispanEmbeddedHttpSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//this enables spring-session
@EnableInfinispanEmbeddedHttpSession
@Configuration
public class InfinispanConfiguration {

   /*
    * Spring Session is based on Spring Caching for JDG/Infinispan. We need to configure it.
    */
   @Bean
   public SpringEmbeddedCacheManagerFactoryBean springCache() {
      return new SpringEmbeddedCacheManagerFactoryBean();
   }

}
