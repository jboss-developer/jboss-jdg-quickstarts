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

import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.jboss.ejb.client.ContextSelector;
import org.jboss.ejb.client.EJBClientConfiguration;
import org.jboss.ejb.client.EJBClientContext;
import org.jboss.ejb.client.PropertiesBasedEJBClientConfiguration;
import org.jboss.ejb.client.remoting.ConfigBasedEJBClientContextSelector;

/**
 * <p>
 * A simple standalone application which uses the JBoss API to invoke the AppOne demonstration Bean.
 * </p>
 * 
 * @author <a href="mailto:wfink@redhat.com">Wolf-Dieter Fink</a>
 */
public class AppOneClient {

   /**
    * @param args
    *           optional to override the connect parameter
    *           <ul>
    *           <li>hostname for AdminApp (default localhost)</li>
    *           <li>port for AdminApp (default 4447)</li>
    *           <li>hostname for AppOne (default localhost)</li>
    *           <li>port for AppOne (default 4547)</li>
    *           </ul>
    * @throws Exception
    */
   public static void main(String[] args) throws Exception {
      String hostAdmin = "localhost";
      String portAdmin = "8080";
      String hostAppOne = "localhost";
      String portAppOne = "8180";

      // suppress output of client messages
      Logger.getLogger("org.jboss").setLevel(Level.OFF);
      Logger.getLogger("org.xnio").setLevel(Level.OFF);

      if (args.length > 0) {
         hostAdmin = args[0];
      }
      if (args.length > 1) {
         portAdmin = args[1];
      }
      if (args.length > 2) {
         hostAppOne = args[2];
      }
      if (args.length > 3) {
         portAppOne = args[3];
      }

      Properties p = new Properties();
      p.put("remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED", "false");
      p.put("remote.connections", "admin,appOne");
      p.put("remote.connection.admin.port", portAdmin);
      p.put("remote.connection.admin.host", hostAdmin);
      p.put("remote.connection.admin.username", "quickuser");
      p.put("remote.connection.admin.password", "quick-123");
      p.put("remote.connection.appOne.port", portAppOne);
      p.put("remote.connection.appOne.host", hostAppOne);
      p.put("remote.connection.appOne.username", "quickuser");
      p.put("remote.connection.appOne.password", "quick-123");

      EJBClientConfiguration cc = new PropertiesBasedEJBClientConfiguration(p);
      ContextSelector<EJBClientContext> selector = new ConfigBasedEJBClientContextSelector(cc);
      EJBClientContext.setSelector(selector);

      Properties props = new Properties();
      props.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
      InitialContext context = new InitialContext(props);

      System.out.println("        Add a value to App2Cache with the AdminApp");

      final String adminLookup = "ejb:jboss-eap-application-adminApp/ejb//CacheAdminBean!" + CacheAdmin.class.getName();
      final CacheAdmin admin = (CacheAdmin) context.lookup(adminLookup);
      admin.addToApp2Cache("App2Entry", "The App2 entry");
      // check that the Admin has the correct key entry local
      admin.verifyApp2Cache("App2Entry", "The App2 entry");

      final String appOneLookup = "ejb:jboss-eap-application-AppOne/ejb//AppOneCacheAccessBean!"
            + AppOneCacheAccess.class.getName();
      final AppOneCacheAccess appOne = (AppOneCacheAccess) context.lookup(appOneLookup);

      System.out.println("        Access the App2Cache from the AppOneServer by using the clustered EJB@AppTwoServer");
      // run AppOne remote access to AppTwo and provide the cluster nodes 
      Set<String> app2nodes = appOne.verifyApp2CacheRemote("App2Entry", "The App2 entry");

      System.out.println("          success : received the following node names for EJB invocation : " + app2nodes);
   }

}
