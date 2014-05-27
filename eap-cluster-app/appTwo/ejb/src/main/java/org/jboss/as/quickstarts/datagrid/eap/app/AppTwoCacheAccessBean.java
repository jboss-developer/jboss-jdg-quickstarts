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

import javax.ejb.Stateless;
import javax.inject.Inject;

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
public class AppTwoCacheAccessBean implements AppTwoCacheAccess {
   private static final Logger LOGGER = Logger.getLogger(AppTwoCacheAccessBean.class);

   @Inject
   DefaultCacheManager appCacheManager;

   @Override
   public String verifyApp2Cache(String key, String value) {
      Cache<String, String> cache = appCacheManager.getCache("App2Cache");
      final String cacheValue = cache.get(key);
      if ((value == null && cacheValue != null) || (value != null && !value.equals(cacheValue))) {
         throw new IllegalStateException("The given key/value pair does not match the cache!");
      }
      return System.getProperty("jboss.node.name");
   }
}
