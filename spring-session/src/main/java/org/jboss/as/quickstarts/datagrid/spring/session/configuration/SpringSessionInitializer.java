//package org.jboss.as.quickstarts.datagrid.spring.session.configuration;
//
//import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;
//
///**
// * This class has been intentionally commented out.
// *
// * When when one can not use Spring Boot (and uses Spring Beans/Context/Web configured manually),
// * this class is essential to tell Spring Session how to hook into the container.
// *
// * Unfortunately adding it to Spring Boot-enabled deployment will cause a failure (since you can not have
// * two <code>WebApplicationInitializer</code> instances in the same deployment.
// */
//public class SpringSessionInitializer extends AbstractHttpSessionApplicationInitializer {
//
//   public SpringSessionInitializer() {
//      super(InfinispanConfiguration.class);
//   }
//}
