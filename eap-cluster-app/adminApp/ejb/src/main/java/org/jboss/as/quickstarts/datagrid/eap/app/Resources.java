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

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.infinispan.manager.DefaultCacheManager;

/**
 * 
 * Producer for cache manager injection.
 * 
 * @author "Wolf-Dieter Fink"
 */
public final class Resources {

   @Inject
   App1CacheManagerProvider cacheManagerProvider;
   @Inject
   App2CacheManagerProvider cacheManagerProvider2;

   @Produces
   DefaultCacheManager getApp1CacheManager() {
      return cacheManagerProvider.getCacheManager();
   }

   @Produces
   @App2Cache
   DefaultCacheManager getApp2CacheManager() {
      return cacheManagerProvider2.getCacheManager();
   }

}
