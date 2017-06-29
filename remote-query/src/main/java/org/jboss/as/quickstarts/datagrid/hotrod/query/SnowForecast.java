/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
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
package org.jboss.as.quickstarts.datagrid.hotrod.query;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.infinispan.client.hotrod.Flag;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.Search;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.marshall.ProtoStreamMarshaller;
import org.infinispan.protostream.SerializationContext;
import org.infinispan.protostream.annotations.ProtoSchemaBuilder;
import org.infinispan.query.api.continuous.ContinuousQuery;
import org.infinispan.query.api.continuous.ContinuousQueryListener;
import org.infinispan.query.dsl.Expression;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryFactory;
import org.infinispan.query.remote.client.ProtobufMetadataManagerConstants;
import org.jboss.as.quickstarts.datagrid.hotrod.query.domain.Forecast;

/**
 * A simple demo for aggregations and continuous query capabilities on a remote cache.
 *
 * @author Adrian Nistor
 */
public class SnowForecast {

   private static final String SERVER_HOST = "jdg.host";
   private static final String HOTROD_PORT = "jdg.hotrod.port";
   private static final String CACHE_NAME = "jdg.cache";
   private static final String PROPERTIES_FILE = "jdg.properties";

   private static final String APP_MENU = "\nAvailable actions:\n" +
         "0. Display available actions\n" +
         "1. Add/update forecast\n" +
         "2. Remove forecast\n" +
         "3. Add continuous query listener\n" +
         "4. Remove continuous query listener\n" +
         "5. Display snow report\n" +
         "6. Display all cache entries\n" +
         "7. Clear cache\n" +
         "8. Quit\n";

   private RemoteCacheManager cacheManager;

   /**
    * A cache that holds Forecast objects.
    */
   private RemoteCache<Integer, Forecast> remoteCache;

   private ContinuousQuery<Integer, Forecast> continuousQuery;

   public SnowForecast() throws Exception {
      final String host = jdgProperty(SERVER_HOST);
      final int hotrodPort = Integer.parseInt(jdgProperty(HOTROD_PORT));
      final String cacheName = jdgProperty(CACHE_NAME);  // The name of the address book  cache, as defined in your server config.

      System.out.printf("Using cache %s on %s:%d\n\n", cacheName, host, hotrodPort);

      ConfigurationBuilder builder = new ConfigurationBuilder();
      builder.addServer()
            .host(host)
            .port(hotrodPort)
            .marshaller(new ProtoStreamMarshaller());  // The Protobuf based marshaller is required for query capabilities
      cacheManager = new RemoteCacheManager(builder.build());

      remoteCache = cacheManager.getCache(cacheName);
      if (remoteCache == null) {
         throw new RuntimeException("Cache '" + cacheName + "' not found. Please make sure the server is properly configured");
      }

      registerSchemasAndMarshallers();
   }

   /**
    * Register the Protobuf schemas and marshallers with the client and then register the schemas with the server too.
    */
   private void registerSchemasAndMarshallers() throws IOException {
      // Register entity marshallers on the client side ProtoStreamMarshaller instance associated with the remote cache manager.
      SerializationContext ctx = ProtoStreamMarshaller.getSerializationContext(cacheManager);

      // generate the 'memo.proto' schema file based on the annotations on Memo class and register it with the SerializationContext of the client
      ProtoSchemaBuilder protoSchemaBuilder = new ProtoSchemaBuilder();
      String forecastSchemaFile = protoSchemaBuilder
            .fileName("forecast.proto")
            .packageName("quickstart")
            .addClass(Forecast.class)
            .build(ctx);

      // register the schemas with the server too
      RemoteCache<String, String> metadataCache = cacheManager.getCache(ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME);
      metadataCache.put("forecast.proto", forecastSchemaFile);
      String errors = metadataCache.get(ProtobufMetadataManagerConstants.ERRORS_KEY_SUFFIX);
      if (errors != null) {
         throw new IllegalStateException("Some Protobuf schema files contain errors:\n" + errors);
      }
   }

   private void addContinuousQueryListener() {
      if (continuousQuery == null) {
         QueryFactory qf = Search.getQueryFactory(remoteCache);

         Query query = qf.from(Forecast.class)
               .having("humidity").lte(75.0f)
               .and().having("temperature").lte(3.0f)
               .and().having("rain").eq(0)
               .and().having("snowfall").gte(0)
               .toBuilder().build();

         continuousQuery = Search.getContinuousQuery(remoteCache);

         ContinuousQueryListener<Integer, Forecast> cqListener = new ContinuousQueryListener<Integer, Forecast>() {

            @Override
            public void resultJoining(Integer key, Forecast f) {
               System.out.printf("Great news! Found perfect ski conditions at '%s' in %d-%d-%d\n", f.getLocation(), f.getYear(), f.getMonth(), f.getDay());
            }

            @Override
            public void resultLeaving(Integer key) {
               System.out.printf("The forecast %s was updated (or removed) and it no longer predicts good ski conditions\n", key);
            }
         };

         continuousQuery.addContinuousQueryListener(query, cqListener);
         System.out.println("Continuous query listener added.");
      }
   }

   private void removeContinuousQueryListener() {
      if (continuousQuery != null) {
         continuousQuery.removeAllListeners();
         continuousQuery = null;
         System.out.println("Continuous query listener removed.");
      }
   }

   private void addForecast() throws ParseException {
      int id = Integer.parseInt(readConsole("Enter forecast id (int): "));
      String location = readConsole("Enter location (string): ");
      int year = Integer.parseInt(readConsole("Enter year (int): "));
      int month = Integer.parseInt(readConsole("Enter month (int): "));
      int day = Integer.parseInt(readConsole("Enter day (int): "));
      float rain = Float.parseFloat(readConsole("Rain mm (float): "));
      float snow = Float.parseFloat(readConsole("Snowfall cm (float): "));
      float temperature = Float.parseFloat(readConsole("Temperature (float): "));
      float humidity = Float.parseFloat(readConsole("Humidity (float): "));
      Forecast forecast = new Forecast();
      forecast.setId(id);
      forecast.setLocation(location);
      forecast.setYear(year);
      forecast.setMonth(month);
      forecast.setDay(day);
      forecast.setRain(rain);
      forecast.setSnowfall(snow);
      forecast.setTemperature(temperature);
      forecast.setHumidity(humidity);

      if (remoteCache.containsKey(forecast.getId())) {
         System.out.println("Updating forecast with id " + forecast.getId());
      }

      // put the Forecast in cache
      remoteCache.put(forecast.getId(), forecast);
   }

   private void removeForecast() {
      int id = Integer.parseInt(readConsole("Enter forecast id to remove (int): "));

      // remove from cache
      Forecast prevValue = remoteCache.withFlags(Flag.FORCE_RETURN_VALUE).remove(id);
      System.out.println("Removed: " + prevValue);
   }

   private void printSnowReport() {
      QueryFactory qf = Search.getQueryFactory(remoteCache);
      Query q = qf.from(Forecast.class)
            .select(Expression.property("location"), Expression.property("year"), Expression.property("month"),
                  Expression.property("day"), Expression.sum("rain"), Expression.sum("snowfall"),
                  Expression.avg("humidity"), Expression.avg("temperature"))
            .groupBy("location", "year", "month", "day")
            .build();

      List<Object[]> list = q.list();
      System.out.println("[Location, year, month, day, rain, snowfall, humidity, temperature]");
      for (Object[] row : list) {
         System.out.println(Arrays.asList(row));
      }
   }

   private void printAllEntries() {
      for (Object key : remoteCache.keySet()) {
         System.out.printf("key=%s, value=%s\n", key, remoteCache.get(key));
      }
   }

   private void cleaCache() {
      remoteCache.clear();
      System.out.println("Cache cleared.");
   }

   private void stop() {
      cacheManager.stop();
   }

   public static void main(String[] args) throws Exception {
      SnowForecast forecast = new SnowForecast();
      System.out.println(APP_MENU);

      while (true) {
         try {
            String action = readConsole("> ");
            if (action == null) {
               continue;
            }
            action = action.trim();
            if (action.isEmpty()) {
               continue;
            }

            if ("0".equals(action)) {
               System.out.println(APP_MENU);
            } else if ("1".equals(action)) {
               forecast.addForecast();
            } else if ("2".equals(action)) {
               forecast.removeForecast();
            } else if ("3".equals(action)) {
               forecast.addContinuousQueryListener();
            } else if ("4".equals(action)) {
               forecast.removeContinuousQueryListener();
            } else if ("5".equals(action)) {
               forecast.printSnowReport();
            } else if ("6".equals(action)) {
               forecast.printAllEntries();
            } else if ("7".equals(action)) {
               forecast.cleaCache();
            } else if ("8".equals(action)) {
               System.out.println("Bye!");
               break;
            } else {
               System.out.println("\nUnrecognized action!");
               System.out.println(APP_MENU);
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
      }

      forecast.stop();
   }

   private static String readConsole(String prompt) {
      // this method is intended to be as simple as possible rather than
      // being efficient by caching a reference to the console/buffered reader

      Console con = System.console();
      if (con != null) {
         return con.readLine(prompt);
      }

      System.out.print(prompt);
      try {
         BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
         return reader.readLine();
      } catch (IOException ex) {
         throw new IOError(ex);
      }
   }

   private String jdgProperty(String name) {
      InputStream res = null;
      try {
         res = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE);
         Properties props = new Properties();
         props.load(res);
         return props.getProperty(name);
      } catch (IOException ioe) {
         throw new RuntimeException(ioe);
      } finally {
         if (res != null) {
            try {
               res.close();
            } catch (IOException e) {
               // ignore
            }
         }
      }
   }
}
