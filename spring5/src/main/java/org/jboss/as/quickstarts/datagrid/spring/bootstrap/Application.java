package org.jboss.as.quickstarts.datagrid.spring.bootstrap;

import org.jboss.as.quickstarts.datagrid.spring.core.configuration.DomainConfig;
import org.jboss.as.quickstarts.datagrid.spring.rest.configuration.RestConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;


/**
 * Spring boot starting point.
 *
 * @author Sebastian Laskawiec
 */
@SpringBootApplication
@Import({ DomainConfig.class, RestConfiguration.class})
public class Application {

   public static void main(String[] args) {
      SpringApplication.run(Application.class, args);
   }
}
