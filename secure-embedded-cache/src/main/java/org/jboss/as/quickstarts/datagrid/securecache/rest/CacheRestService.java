package org.jboss.as.quickstarts.datagrid.securecache.rest;

import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.auth.Subject;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.infinispan.Cache;
import org.infinispan.manager.EmbeddedCacheManager;
import org.jboss.security.SecurityContextAssociation;
import org.jboss.security.SimpleGroup;
import org.jboss.security.SimplePrincipal;

import javax.inject.Named;

@ApplicationScoped
@Path("/cache")
public class CacheRestService {

	@Inject
	@Named("cacheManager")
	private EmbeddedCacheManager cm;

	@GET
	@Path("/get")
	@Produces("application/json")
	public CacheOperationResult<CacheEntry<String,String>> get(final @QueryParam("key") String key) {
		final Cache<String, String> cache;
		final CacheOperationResult<CacheEntry<String,String>> cor = new CacheOperationResult<CacheEntry<String,String>>();
		Subject subject = SecurityContextAssociation.getSubject();
		try {
			cache = cm.getCache("secured");
			Subject.doAs(subject, new PrivilegedAction<Void>() {
				public Void run() {	
					ArrayList<CacheEntry<String, String>> cacheEntries = new ArrayList<CacheEntry<String, String>>();
					/* 
					 * If key is provided, extract the value associated with the key in the
					 * cache, else get all the entries
					 */
					if (key == null) {
						Set<Map.Entry<String, String>> entries = cache.entrySet();
						for (Map.Entry<String, String> entry : entries) {
							cacheEntries.add(new CacheEntry<String, String>(entry.getKey(), entry.getValue()));
						}
					} else {
						String value = cache.get(key);
						if (value != null)
							cacheEntries.add(new CacheEntry<String, String>(key, value));
					}
					
					/*
					 * Sort all the cache entries base on the key value
					 */
					Collections.sort(cacheEntries);
					cor.setOutputEntries(cacheEntries);
					return null;
				}
			});	
		} catch(Exception e) {
			cor.setFailed(true);
			cor.setFailureMessage(e.getMessage());
		}
		return cor;
	}

	@PUT
	@Path("/put")
	@Produces("application/json")
	public CacheOperationResult<String> put(final @QueryParam("key") String key, 
			final @QueryParam("value") String value) {
		final Cache<String, String> cache;
		final CacheOperationResult<String> cor = new CacheOperationResult<String>();
		Subject subject = SecurityContextAssociation.getSubject();
		try {
			cache = cm.getCache("secured");
			String returnValue = Subject.doAs(subject, new PrivilegedAction<String>() {
				public String run() {	
					return cache.putIfAbsent(key, value);
				}
			});	
			ArrayList<String> returnValues= new ArrayList<String>();
			returnValues.add(returnValue);
			cor.setOutputEntries(returnValues);
		} catch (Exception e) {
			cor.setFailed(true);
			cor.setFailureMessage(e.getMessage());
		}
		return cor;
	}

	@GET
	@Path("/loggedUser")
	@Produces({ MediaType.TEXT_PLAIN })
	public String loggedUser() {
		String returnValue = "Logged in User: ";
		Subject subject = SecurityContextAssociation.getSubject();
		Set<Principal> principals = subject.getPrincipals();
		for (Principal p : principals) {
			if(p instanceof SimplePrincipal 
					&& !p.getName().equals("Roles") 
					&& !p.getName().equals("CallerPrincipal")) {
				SimplePrincipal sp = (SimplePrincipal) p;
				returnValue = returnValue.concat(sp.getName());
			} 
			
			if (p instanceof SimpleGroup && p.getName().equals("Roles")) {
				SimpleGroup sg = (SimpleGroup) p;
				@SuppressWarnings("rawtypes")
				Enumeration members = sg.members();
				HashSet<String> roles = new HashSet<String>();
				while(members.hasMoreElements()) {
					Object obj = members.nextElement();
					if(obj instanceof SimplePrincipal) {
						roles.add(((SimplePrincipal) obj).getName());
					}
				}
				returnValue = returnValue.concat(", Roles :"+roles);
			} 
		}
		
		return returnValue;
	}

	@DELETE
	@Path("/remove")
	@Produces("application/json")
	public CacheOperationResult<Boolean> remove(final @QueryParam("key") String key,
			final @QueryParam("value") String value) {
		final Cache<String, String> cache;
		final CacheOperationResult<Boolean> cor = new CacheOperationResult<Boolean>();
		Subject subject = SecurityContextAssociation.getSubject();
		try {		
			cache = cm.getCache("secured");
			Boolean returnValue = Subject.doAs(subject, new PrivilegedAction<Boolean>() {
				public Boolean run() {	
					return cache.remove(key, value);
				}
			});
			ArrayList<Boolean> returnValues= new ArrayList<Boolean>();
			returnValues.add(returnValue);
			cor.setOutputEntries(returnValues);
		} catch (Exception e) {
			cor.setFailed(true);
			cor.setFailureMessage(e.getMessage());
		}
		return cor;	
	}
}
