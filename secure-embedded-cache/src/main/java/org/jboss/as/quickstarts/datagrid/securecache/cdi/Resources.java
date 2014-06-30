package org.jboss.as.quickstarts.datagrid.securecache.cdi;

import java.io.IOException;
import java.security.PrivilegedAction;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;

import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.security.Security;
import org.jboss.security.SecurityContextAssociation;

import javax.inject.Named;
import javax.security.auth.Subject;

public class Resources {
	
	private static EmbeddedCacheManager ecm;

	@Named
	@Produces
	@Default
	public EmbeddedCacheManager cacheManager() throws IOException {
		if(ecm == null) {
			Subject subject = SecurityContextAssociation.getSubject();
			Security.doAs(subject, new PrivilegedAction<Void>() {
				public Void run() {
					try {
						ecm = new DefaultCacheManager("infinispan.xml");
					} catch (IOException e) {
						e.printStackTrace();
					}
					ecm.start();
					return null;
				}
			});
		}
		return ecm;
	}
}
