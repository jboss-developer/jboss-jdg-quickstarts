package org.jboss.as.quickstarts.camel_infinispan.rest;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.infinispan.Cache;
import org.infinispan.manager.EmbeddedCacheManager;
import org.jboss.as.quickstarts.camel_infinispan.pojos.PersonPojo;

import javax.inject.Named;

@ApplicationScoped
@Path("/cache")
public class CacheRestService {

    @Inject
    @Named("cacheManager")
    private EmbeddedCacheManager cm;
    
    @GET
    @Path("/getPersonById")
    @Produces("application/json")
    public PersonPojo getPersonById(@QueryParam(value = "id") String id) {
        Cache<String, PersonPojo> cache = cm.getCache("camel-cache");
        return cache.get(id);
    }

    @GET
    @Path("/getEveryone")
    @Produces("application/json")
    public List<PersonPojo> getEveryone() {
        Cache<String, PersonPojo> cache = cm.getCache("camel-cache");
        
        // Call to cache.values() is strongly *NOT* recommended. It is done 
        // here for convenience as this project is meant to be a demo
        return new ArrayList<PersonPojo>(cache.values());
    }

}
