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

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.jboss.logging.Logger;

/**
 * <p>
 * The administration bean called by the standalone client.
 * </p>
 * <p>
 * Used to create check and remove entries to the different caches
 * </p>
 * 
 * @author <a href="mailto:wfink@redhat.com">Wolf-Dieter Fink</a>
 */
@Stateless
public class CacheAdminBean implements CacheAdmin {
   private static final Logger LOGGER = Logger.getLogger(CacheAdminBean.class);
   @Resource
   SessionContext context;

   @Inject
   DefaultCacheManager app1CacheManager;

   @Inject
   @App2Cache
   DefaultCacheManager app2CacheManager;

   /**
    * Initialize and store the context for the EJB invocations.
    */
   @PostConstruct
   public void init() {
      LOGGER.info("CacheManagers:");
      LOGGER.info("App1 " + app1CacheManager);
      LOGGER.info("App2 " + app2CacheManager);
   }

   @Override
   public void addToApp1Cache(String key, String value) {
      LOGGER.info("addTo app1Cache (" + key + "," + value + ")");
      Cache<String, String> cache = app1CacheManager.getCache("App1Cache");
      cache.put(key, value);
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
   public String getFromApp1Cache(String key) {
      LOGGER.info("getFrom progCache(" + key + ")");
      Cache<String, String> cache = app1CacheManager.getCache("App1Cache");
      final String value = cache.get(key);
      LOGGER.info("value=" + value);

      final String nodeName = System.getProperty("jboss.node.name");
      return "Read app1Cache for key=" + key + " at server '" + nodeName + "' and get "
            + (value == null ? "no value" : "value=" + value);
   }

   @Override
   public void addToApp2Cache(String key, String value) {
      LOGGER.info("addTo app2Cache (" + key + "," + value + ")");
      Cache<String, String> cache = app2CacheManager.getCache("App2Cache");
      cache.put(key, value);
   }

   @Override
   public void removeFromApp2Cache(String key) {
      LOGGER.info("removeFrom app2Cache (" + key + ")");
      Cache<String, String> cache = app2CacheManager.getCache("App2Cache");
      cache.remove(key);
   }

   @Override
   @TransactionAttribute(TransactionAttributeType.REQUIRED)
   public void addToApp2Cache(String key, String value, boolean transactionFailure) {
      Cache<String, String> cache = app2CacheManager.getCache("App2Cache");
      if (transactionFailure && cache.containsKey(key)) {
         // ensure that the key does not exists before we add it 
         throw new IllegalStateException("Key " + key + " exists!");
      }
      addToApp2Cache(key, value);

      if (transactionFailure) {
         // set the rollback flag and let the transaction fail
         context.setRollbackOnly();
      }
   }

   @Override
   public boolean containsApp2Key(String key) {
      Cache<String, String> cache = app2CacheManager.getCache("App2Cache");
      final boolean contains = cache.containsKey(key);
      LOGGER.info("app2Cache " + (contains ? "" : "does not ") + "contain a entity with key=" + key);
      return contains;
   }

   @Override
   public void verifyApp2Cache(String key, String value) {
      Cache<String, String> cache = app2CacheManager.getCache("App2Cache");
      final String cacheValue = cache.get(key);
      if ((value == null && cacheValue != null) || (value != null && !value.equals(cacheValue))) {
         throw new IllegalStateException("The given key/value pair does not match the cache!");
      }
   }

}
