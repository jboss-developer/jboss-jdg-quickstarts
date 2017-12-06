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
package org.infinispan.quickstart.cdi.test;

import org.infinispan.eviction.EvictionStrategy;
import org.infinispan.quickstart.cdi.GreetingCacheManager;
import org.infinispan.quickstart.cdi.GreetingService;
import org.infinispan.quickstart.cdi.test.util.Deployments;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;

/**
 * @author Kevin Pollet <pollet.kevin@gmail.com> (C) 2011
 */
@RunWith(Arquillian.class)
public class GreetingCacheManagerTest {

    @Deployment
    public static WebArchive deployment() {
        return Deployments.baseDeployment()
                .addClass(GreetingCacheManagerTest.class);
    }

    @Inject
    private GreetingService greetingService;

    @Inject
    private GreetingCacheManager greetingCacheManager;

    @Test
    public void testGreetingCacheConfiguration() {
        assertEquals("greeting-cache", greetingCacheManager.getCacheName());
        assertEquals(4, greetingCacheManager.getEvictionMaxEntries());
        assertEquals(EvictionType.COUNT, greetingCacheManager.getEvictionType());

    }

    @Test
    public void testGreetingCacheCachedValues() {
        greetingService.greet("Pete");

        assertEquals(1, greetingCacheManager.getCachedValues().length);
        assertEquals("Hello Pete :)", greetingCacheManager.getCachedValues()[0]);
    }

    @Test
    public void testClearGreetingCache() {
        greetingService.greet("Pete");

        assertEquals(1, greetingCacheManager.getNumberOfEntries());

        greetingCacheManager.clearCache();

        assertEquals(0, greetingCacheManager.getNumberOfEntries());
    }
}
