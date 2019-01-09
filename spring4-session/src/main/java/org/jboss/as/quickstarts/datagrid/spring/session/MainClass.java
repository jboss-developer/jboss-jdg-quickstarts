package org.jboss.as.quickstarts.datagrid.spring.session;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

/**
 * This is the main entry point for enabling Spring Session in Spring Boot-based application.
 *
 * <p>
 *    This app can be ran in standalone mode (using <code>mvn spring-boot:run</code>) or deployed to JWS/Tomcat.
 *    Spring Boot Maven plugin will invoke the <code>main</code> method whereas JWS/Tomcat will use
 *    <code>SpringBootServletInitializer</code> (which implements <code>WebApplicationInitializer</code>).
 * </p>
 *
 * <p>
 *    It is also possible to use this skeleton app as for deploying using manual Spring configuration (without
 *    Spring Boot). In that case, make sure you create <code>SpringSessionInitializer</code> class in your
 *    deployment (for the sake of this demo it has been commented out).
 * </p>
 */
@SpringBootApplication
public class MainClass extends SpringBootServletInitializer {

   public static void main(String... args) {
      SpringApplication.run(MainClass.class, args);
   }

   protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
      return application.sources(MainClass.class);
   }
}
