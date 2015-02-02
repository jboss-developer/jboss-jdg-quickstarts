package org.jboss.as.quickstarts.camel_infinispan.listener;

import org.apache.camel.component.gson.GsonDataFormat;
import org.apache.camel.component.servletlistener.CamelContextLifecycle;
import org.apache.camel.component.servletlistener.ServletCamelContext;
import org.apache.camel.impl.JndiRegistry;
import org.infinispan.manager.EmbeddedCacheManager;
import org.jboss.as.quickstarts.camel_infinispan.cdi.Resources;
import org.jboss.as.quickstarts.camel_infinispan.pojos.PersonPojo;

public class InfinispanCamelContextLifecycle implements CamelContextLifecycle<JndiRegistry> {
	
	@Override
	public void afterAddRoutes(ServletCamelContext camelContext, JndiRegistry registry)
			throws Exception {
	}

	@Override
	public void afterStart(ServletCamelContext camelContext, JndiRegistry registry)
			throws Exception {
	}

	@Override
	public void afterStop(ServletCamelContext camelContext, JndiRegistry registry)
			throws Exception {
	}

	@Override
	public void beforeAddRoutes(ServletCamelContext camelContext, JndiRegistry registry)
			throws Exception {
		
		// The better obvious way of doing this with CDI's DI but that approach failed  
		// here because of the manner this listener is processed by Camel 
		EmbeddedCacheManager cm = new Resources().cacheManager();
		cm.getCache();
		registry.bind("cacheManager", cm);
		
		// Couldn't find any better place to instantiate and bind this data format  
		// to the registry
		GsonDataFormat json = new GsonDataFormat(PersonPojo.class);
		registry.bind("json", json);
		
	}

	@Override
	public void beforeStart(ServletCamelContext camelContext, JndiRegistry registry)
			throws Exception {
	}

	@Override
	public void beforeStop(ServletCamelContext camelContext, JndiRegistry registry)
			throws Exception {
		EmbeddedCacheManager cm  = (EmbeddedCacheManager) registry.lookup("cacheManager");
		if(cm != null) {
			cm.stop();
		}				
	}

}
