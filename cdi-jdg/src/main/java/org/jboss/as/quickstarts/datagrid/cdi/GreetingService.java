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
package org.jboss.as.quickstarts.datagrid.cdi;

import javax.cache.annotation.CacheResult;

/**
 * <p>This is the Greeting Service class.</p>
 *
 * <p>Each call to the {@link GreetingService#greet(String)} method will be cached in the greeting-cache (in this case
 * the {@linkplain javax.cache.annotation.CacheKey CacheKey} will be the name). If this method has been already called
 * with the same name the cached value will be returned and this method will not be called.</p>
 *
 * @author Kevin Pollet <pollet.kevin@gmail.com> (C) 2011
 * @see CacheResult
 */
public class GreetingService {

   @CacheResult(cacheName = "greeting-cache")
   public String greet(String name) {
      return "Hello " + name + " :)";
   }

}
