package org.jboss.as.quickstarts.datagrid.spring.session.configuration;

import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;

public class SpringSessionInitializer extends AbstractHttpSessionApplicationInitializer {

   public SpringSessionInitializer() {
      super(InfinispanConfiguration.class);
   }
}
