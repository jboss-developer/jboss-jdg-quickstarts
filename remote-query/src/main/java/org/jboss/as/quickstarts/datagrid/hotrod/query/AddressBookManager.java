/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
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

import com.google.protobuf.Descriptors;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.Search;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.marshall.ProtoStreamMarshaller;
import org.infinispan.commons.util.Util;
import org.infinispan.protostream.SerializationContext;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryFactory;
import org.jboss.as.quickstarts.datagrid.hotrod.query.domain.Person;
import org.jboss.as.quickstarts.datagrid.hotrod.query.domain.PhoneNumber;
import org.jboss.as.quickstarts.datagrid.hotrod.query.domain.PhoneType;
import org.jboss.as.quickstarts.datagrid.hotrod.query.marshallers.PersonMarshaller;
import org.jboss.as.quickstarts.datagrid.hotrod.query.marshallers.PhoneNumberMarshaller;
import org.jboss.as.quickstarts.datagrid.hotrod.query.marshallers.PhoneTypeMarshaller;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.BufferedReader;
import java.io.Console;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Adrian Nistor
 */
public class AddressBookManager {

   private static final String JDG_HOST = "jdg.host";
   private static final String HOTROD_PORT = "jdg.hotrod.port";
   private static final String JMX_PORT = "jdg.jmx.port";
   private static final String PROPERTIES_FILE = "jdg.properties";
   private static final String CACHE_CONTAINER_NAME = "local";
   private static final String CACHE_NAME = "addressbook";
   private static final String PROTOBUF_DESCRIPTOR_RESOURCE = "/quickstart/addressbook.protobin";

   private static final String menu = "\nAvailable actions:\n" +
         "0. Display available actions\n" +
         "1. Add person\n" +
         "2. Remove person\n" +
         "3. Add phone\n" +
         "4. Remove phone\n" +
         "5. Display all persons\n" +
         "6. Query persons by name\n" +
         "7. Query persons by phone\n" +
         "8. Quit\n";

   private RemoteCacheManager cacheManager;
   private RemoteCache<Integer, Person> cache;

   public AddressBookManager() throws Exception {
      final String host = jdgProperty(JDG_HOST);
      final int hotrodPort = Integer.parseInt(jdgProperty(HOTROD_PORT));
      final int jmxPort = Integer.parseInt(jdgProperty(JMX_PORT));

      ConfigurationBuilder builder = new ConfigurationBuilder();
      builder.addServer()
            .host(host)
            .port(hotrodPort)
            .marshaller(new ProtoStreamMarshaller());
      cacheManager = new RemoteCacheManager(builder.build());

      cache = cacheManager.getCache(CACHE_NAME);

      registerProtofile(host, jmxPort, CACHE_CONTAINER_NAME);

      registerMarshallers(cacheManager);
   }

   /**
    * Register the Protobuf descriptors file on the server via JMX.
    */
   private void registerProtofile(String jmxHost, int jmxPort, String cacheContainerName) throws Exception {
      JMXConnector jmxConnector = JMXConnectorFactory.connect(new JMXServiceURL("service:jmx:remoting-jmx://" + jmxHost + ":" + jmxPort));
      MBeanServerConnection jmxConnection = jmxConnector.getMBeanServerConnection();

      ObjectName protobufMetadataManagerObjName = new ObjectName("jboss.infinispan:type=RemoteQuery,name="
                                                + ObjectName.quote(cacheContainerName) + ",component=ProtobufMetadataManager");

      //initialize client-side serialization context via JMX
      byte[] descriptor = readClasspathResource(PROTOBUF_DESCRIPTOR_RESOURCE);
      jmxConnection.invoke(protobufMetadataManagerObjName, "registerProtofile", new Object[]{descriptor}, new String[]{byte[].class.getName()});
      jmxConnector.close();
   }

   private byte[] readClasspathResource(String c) throws IOException {
      InputStream is = getClass().getResourceAsStream(c);
      try {
         return Util.readStream(is);
      } finally {
         if (is != null) {
            is.close();
         }
      }
   }

   /**
    * Register entity marshallers on the client side ProtoStreamMarshaller instance associated with the remote cache manager.
    */
   private void registerMarshallers(RemoteCacheManager cacheManager) throws IOException, Descriptors.DescriptorValidationException {
      SerializationContext ctx = ProtoStreamMarshaller.getSerializationContext(cacheManager);
      ctx.registerProtofile(PROTOBUF_DESCRIPTOR_RESOURCE);
      ctx.registerMarshaller(Person.class, new PersonMarshaller());
      ctx.registerMarshaller(PhoneNumber.class, new PhoneNumberMarshaller());
      ctx.registerMarshaller(PhoneType.class, new PhoneTypeMarshaller());
   }

   private void queryByName() {
      String namePattern = readConsole("Enter person name pattern: ");

      QueryFactory qf = Search.getQueryFactory(cache);
      Query query = qf.from(Person.class)
            .having("name").like(namePattern).toBuilder()
            .build();

      List<Person> results = query.list();
      System.out.println("Found " + results.size() + " matches:");
      for (Person p : results) {
         System.out.println(">> " + p);
      }
   }

   private void queryByPhone() {
      String phoneNumber = readConsole("Enter phone number: ");

      QueryFactory qf = Search.getQueryFactory(cache);
      Query query = qf.from(Person.class)
            .having("phone.number").eq(phoneNumber).toBuilder()
            .build();

      List<Person> results = query.list();
      System.out.println("Found " + results.size() + " matches:");
      for (Person p : results) {
         System.out.println(">> " + p);
      }
   }

   private void addPerson() {
      int id = Integer.parseInt(readConsole("Enter person id: "));
      String name = readConsole("Enter person name: ");
      String email = readConsole("Enter person email: ");
      Person person = new Person();
      person.setId(id);
      person.setName(name);
      person.setEmail(email);

      // put the Person in cache
      cache.put(person.getId(), person);
   }

   private void removePerson() {
      int id = Integer.parseInt(readConsole("Enter person id: "));

      // remove from cache
      cache.remove(id);
   }

   private void addPhone() {
      System.out.println("Adding a phone number to a person");
      int id = Integer.parseInt(readConsole("Enter person id: "));
      Person person = cache.get(id);
      if (person == null) {
         System.out.println("Person not found");
         return;
      }
      System.out.println("> " + person);

      String number = readConsole("Enter phone number: ");
      PhoneType type = PhoneType.valueOf(readConsole("Enter phone type [MOBILE, HOME, WORK]: ").toUpperCase());
      List<PhoneNumber> phones = person.getPhones();
      if (phones == null) {
         phones = new ArrayList<PhoneNumber>();
      }
      PhoneNumber phoneNumber = new PhoneNumber();
      phoneNumber.setNumber(number);
      phoneNumber.setType(type);
      phones.add(phoneNumber);
      person.setPhones(phones);

      // update the Person in cache
      cache.put(person.getId(), person);
   }

   private void removePhone() {
      System.out.println("Removing a phone number from a person");
      int id = Integer.parseInt(readConsole("Enter person id: "));
      Person person = cache.get(id);
      if (person == null) {
         System.out.println("Person not found");
         return;
      }
      System.out.println("> " + person);

      if (person.getPhones() != null && !person.getPhones().isEmpty()) {
         int idx = Integer.parseInt(readConsole("Enter phone index [0.." + (person.getPhones().size() - 1) + "]: "));
         if (idx < 0 || idx >= person.getPhones().size()) {
            System.out.println("Index out of range");
            return;
         }
         person.getPhones().remove(idx);

         // update the Person in cache
         cache.put(person.getId(), person);
      } else {
         System.out.println("The person does not have any phones");
      }
   }

   private void printAll() {
      for (int id : cache.keySet()) {
         Person person = cache.get(id);
         System.out.println(person);
      }
   }

   private void stop() {
      cacheManager.stop();
   }

   public static void main(String[] args) throws Exception {
      AddressBookManager manager = new AddressBookManager();
      System.out.println(menu);

      while (true) {
         try {
            String action = readConsole("> ");
            if (action == null) {
               continue;
            }
            action = action.trim();
            if (action.length() == 0) {
               continue;
            }

            if ("0".equals(action)) {
               System.out.println(menu);
            } else if ("1".equals(action)) {
               manager.addPerson();
            } else if ("2".equals(action)) {
               manager.removePerson();
            } else if ("3".equals(action)) {
               manager.addPhone();
            } else if ("4".equals(action)) {
               manager.removePhone();
            } else if ("5".equals(action)) {
               manager.printAll();
            } else if ("6".equals(action)) {
               manager.queryByName();
            } else if ("7".equals(action)) {
               manager.queryByPhone();
            } else if ("8".equals(action)) {
               System.out.println("Bye!");
               break;
            } else {
               System.out.println("\nUnrecognized action!");
               System.out.println(menu);
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
      }

      manager.stop();
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
      Properties props = new Properties();
      try {
         props.load(AddressBookManager.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE));
      } catch (IOException ioe) {
         throw new RuntimeException(ioe);
      }
      return props.getProperty(name);
   }
}
