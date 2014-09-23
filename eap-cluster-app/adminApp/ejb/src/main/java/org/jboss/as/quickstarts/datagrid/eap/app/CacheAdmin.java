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

import javax.ejb.Remote;

@Remote
public interface CacheAdmin {

   /**
    * Add the given entry to the App1Cache
    */
   void addToApp1Cache(String key, String value);

   /**
    * Read from the App1Cache
    * 
    * @param key
    *           the key for the entry to read
    * @return the value for the given key
    */
   String getFromApp1Cache(String key);

   /**
    * Checks whether the given entry with key contains the value in App1Cache
    * 
    * @param key
    * @param value
    */
   void verifyApp1Cache(String key, String value);

   /**
    * Checks whether the given entry with key contains the value in App2Cache
    * 
    * @param key
    * @param value
    */
   void verifyApp2Cache(String key, String value);

   /**
    * Add the given entry to the App2Cache
    */
   void addToApp2Cache(String key, String value);

   /**
    * Add the given entry to the App2Cache and let the transaction fail if requested. As the
    * App2Cache is transactional the entry should not exists if the transaction of the method is
    * marked for rollback after the entry is added to the cache.
    * 
    * @param key
    * @param value
    * @param transactionFailure
    *           If <code>true</code> the transaction will be marked as rollbackOnly
    */
   void addToApp2Cache(String key, String value, boolean transactionFailure);

   /**
    * Check whether the App2Cache contain a entry with the given key
    * 
    * @param key
    *           the key for the entry to check
    * @return <code>true</code> if the entry exists
    */
   boolean containsApp2Key(String key);

   /**
    * Delete the entry from cache
    * 
    * @param key
    *           entry key which should be removed
    */
   void removeFromApp2Cache(String key);
}
