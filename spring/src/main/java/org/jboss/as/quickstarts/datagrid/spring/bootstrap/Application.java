package org.jboss.as.quickstarts.datagrid.spring.bootstrap;

import org.jboss.as.quickstarts.datagrid.spring.core.configuration.DomainConfig;
import org.jboss.as.quickstarts.datagrid.spring.rest.configuration.RestConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Spring boot starting point.
 *
 * @author Sebastian Laskawiec
 */
@Configuration
@Import({ DomainConfig.class, RestConfiguration.class})
@EnableAutoConfiguration
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
