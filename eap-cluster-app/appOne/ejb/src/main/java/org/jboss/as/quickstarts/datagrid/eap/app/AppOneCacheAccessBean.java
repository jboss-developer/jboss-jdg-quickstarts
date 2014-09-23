/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.quickstarts.datagrid.eap.app;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.naming.InitialContext;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.jboss.logging.Logger;

/**
 * <p>
 * The main bean called by the standalone client.
 * </p>
 * <p>
 * The sub applications, deployed in different servers are called direct or via indirect naming to
 * hide the lookup name and use a configured name via comp/env environment.
 * </p>
 * 
 * @author <a href="mailto:wfink@redhat.com">Wolf-Dieter Fink</a>
 */
@Stateless
public class AppOneCacheAccessBean implements AppOneCacheAccess {
   private static final Logger LOGGER = Logger.getLogger(AppOneCacheAccessBean.class);
   @Resource
   SessionContext context;

   @Inject
   DefaultCacheManager app1CacheManager;

   /**
    * The context to invoke foreign EJB's as the SessionContext can not be used for that.
    */
   private InitialContext iCtx;

   @EJB(lookup = "ejb:jboss-eap-application-AppTwo/ejb/AppTwoCacheAccessBean!org.jboss.as.quickstarts.datagrid.eap.app.AppTwoCacheAccess")
   AppTwoCacheAccess appTwoProxy;

   /**
    * Initialize and store the context for the EJB invocations.
    */
   @PostConstruct
   public void init() {
   }

   @Override
   public void addToLocalCache(String key, String value) {
      LOGGER.info("addTo progCache (" + key + "," + value + ")");
      Cache<String, String> cache = app1CacheManager.getCache("progCache");
      cache.put(key, value);
   }

   @Override
   public String getFromLocalCache(String key) {
      LOGGER.info("getFrom progCache(" + key + ")");
      Cache<String, String> cache = app1CacheManager.getCache("progCache");
      final String value = cache.get(key);
      LOGGER.info("value=" + value);

      final String nodeName = System.getProperty("jboss.node.name");
      return "Read progCache for key=" + key + " at server '" + nodeName + "' and get "
            + (value == null ? "no value" : "value=" + value);
   }

   @Override
   public void verifyApp1Cache(String key, String value) {
      Cache<String, String> cache = app1CacheManager.getCache("App1Cache");
      final String cacheValue = cache.get(key);
      if ((value == null && cacheValue != null) || (value != null && !value.equals(cacheValue))) {
         throw new IllegalStateException("The given key/value pair does not match the cache!");
      }
   }

   @Override
   public Set<String> verifyApp2CacheRemote(String key, String value) {
      HashSet<String> results = new HashSet<String>();

      for (int i = 0; i < 10; i++) {
         results.add(this.appTwoProxy.verifyApp2Cache(key, value));
      }
      LOGGER.info("Verified 10 times, see " + results.size() + "node(s) " + results);

      return results;
   }
}
