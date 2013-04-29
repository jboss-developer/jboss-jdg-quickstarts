/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other
 * contributors as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a full listing of
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.quickstarts.datagrid.cdi.config;

import org.infinispan.cdi.ConfigureCache;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.eviction.EvictionStrategy;

import javax.enterprise.inject.Produces;

/**
 * This is the configuration class.
 *
 * @author Kevin Pollet <pollet.kevin@gmail.com> (C) 2011
 * @author Galder Zamarreño
 */
public class Config {

   /**
    * <p>This producer defines the greeting cache configuration.</p>
    *
    * <p>This cache will have:
    * <ul>
    *    <li>a maximum of 4 entries</li>
    *    <li>use the strategy LRU for eviction</li>
    * </ul>
    * </p>
    *
    * @return the greeting cache configuration.
    */
   @GreetingCache
   @ConfigureCache("greeting-cache")
   @Produces
   public Configuration greetingCache() {
      return new ConfigurationBuilder()
            .eviction().strategy(EvictionStrategy.LRU).maxEntries(4)
            .build();
   }

   /**
    * <p>This producer overrides the default cache configuration used by the default cache manager.</p>
    *
    * <p>The default cache configuration defines that a cache entry will have a lifespan of 60000 ms.</p>
    */
   @Produces
   public Configuration defaultCacheConfiguration() {
      return new ConfigurationBuilder()
            .expiration().lifespan(60000l)
            .build();
   }

}
