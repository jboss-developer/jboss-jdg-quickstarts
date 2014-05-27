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

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;

import org.infinispan.manager.DefaultCacheManager;

/**
 * Creates a DefaultCacheManager which is configured with a configuration file.
 * <b>Infinispan's libraries need to be provided as module with dependency, also bundling with the application is possible.</b>
 * 
 * @author Wolf-Dieter Fink
 */
@ApplicationScoped
public class App1CacheManagerProvider {
   private static final String CONFIG = "apponecache-config.xml";
   private static final Logger log = Logger.getLogger(App1CacheManagerProvider.class.getName());
   private DefaultCacheManager manager;

   public DefaultCacheManager getCacheManager() {
      if (manager == null) {
         log.info("construct a App1CacheManager");

         try {
            manager = new DefaultCacheManager(CONFIG, true);
         } catch (IOException e) {
            log.log(Level.SEVERE, "Could not read " + CONFIG + " to create the App1CacheManager", e);
         }
      }
      return manager;
   }

   @PreDestroy
   public void cleanUp() {
      manager.stop();
      manager = null;
   }

}
