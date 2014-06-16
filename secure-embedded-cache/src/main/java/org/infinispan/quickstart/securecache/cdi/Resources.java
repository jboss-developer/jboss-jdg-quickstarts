package org.infinispan.quickstart.securecache.cdi;

import java.io.IOException;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;

import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

import javax.inject.Named;

public class Resources {
	
	private static EmbeddedCacheManager ecm;

	@Named
	@Produces
	@Default
	public EmbeddedCacheManager cacheManager() throws IOException {
		if(ecm == null) {
			ecm = new DefaultCacheManager("infinispan.xml");
			ecm.start();
		}
		return ecm;
	}
}
