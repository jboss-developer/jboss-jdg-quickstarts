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
package org.jboss.as.quickstarts.datagrid.hotrod;

import java.io.Console;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;

import javax.transaction.Status;
import javax.transaction.TransactionManager;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.configuration.TransactionMode;
import org.infinispan.client.hotrod.transaction.lookup.RemoteTransactionManagerLookup;
import org.infinispan.commons.configuration.XMLStringConfiguration;
import org.infinispan.commons.util.CloseableIterator;

/**
 * @author Katia Aresti, karesti@redhat.com
 */
public class FootballManager {

   private static final String JDG_HOST = "jdg.host";
   private static final String HOTROD_PORT = "jdg.hotrod.port";
   private static final String PROPERTIES_FILE = "jdg.properties";
   private static final String msgTeamMissing = "The specified team \"%s\" does not exist, choose next operation\n";
   private static final String msgEnterTeamName = "Enter team name: ";
   private static final String initialPrompt = "Choose action:\n" + "============= \n"
         + "at  -  add a team\n"
         + "ap  -  add a player to a team\n"
         + "rt  -  remove a team\n"
         + "rp  -  remove a player from a team\n"
         + "p   -  print all teams and players\n"
         + "pc  -  print all players and countries\n"
         + "q   -  quit\n";

   private RemoteCacheManager cacheManager;
   private RemoteCache<String, Team> teams;
   private RemoteCache<String, String> players;

   private static final String TEAMS_CACHE = "teams_tx";
   private static final String PLAYERS_CACHE = "players_tx";

   private static final String TEAMS_XML_CACHE_CONFIG =
         "<infinispan><cache-container>" +
               "  <local-cache-configuration name=\"" + TEAMS_CACHE + "\">" +
               "    <locking isolation=\"REPEATABLE_READ\"/>" +
               "    <transaction locking=\"PESSIMISTIC\" mode=\"NON_XA\" />" +
               "  </local-cache-configuration>" +
               "</cache-container></infinispan>";

   private static final String PLAYERS_XML_CACHE_CONFIG =
         "<infinispan><cache-container>" +
               "  <local-cache-configuration name=\"" + PLAYERS_CACHE + "\">" +
               "    <locking isolation=\"REPEATABLE_READ\"/>" +
               "    <transaction locking=\"PESSIMISTIC\" mode=\"NON_XA\" />" +
               "  </local-cache-configuration>" +
               "</cache-container></infinispan>";

   private TransactionManager tm;

   private Console con = System.console();

   public FootballManager() {
      initCaches();
      // Both caches are using the same tx manager so we can keep the reference
      tm = teams.getTransactionManager();
   }

   private void initCaches() {
      ConfigurationBuilder builder = new ConfigurationBuilder();
      builder.addServer()
            .host(jdgProperty(JDG_HOST))
            .port(Integer.parseInt(jdgProperty(HOTROD_PORT)));
      // Configure the RemoteCacheManager to use a transactional cache as default
      // Use the simple TransactionManager in hot rod client
      builder.transaction().transactionManagerLookup(RemoteTransactionManagerLookup.getInstance());
      // The cache will be enlisted as Synchronization
      builder.transaction().transactionMode(TransactionMode.NON_XA);

      cacheManager = new RemoteCacheManager(builder.build());
      // Create teams as a transactional cache
      teams = cacheManager.administration().getOrCreateCache(TEAMS_CACHE, new XMLStringConfiguration(TEAMS_XML_CACHE_CONFIG));
      players = cacheManager.administration().getOrCreateCache(PLAYERS_CACHE, new XMLStringConfiguration(PLAYERS_XML_CACHE_CONFIG));
   }

   public void addTeam() {
      String teamName = con.readLine(msgEnterTeamName);
      try {
         tm.begin();
         teams.putIfAbsent(teamName, new Team(teamName));
      } catch (Exception e) {
         markForRollback(e);
      } finally {
         endTransaction();
      }
   }

   public void addPlayers() {
      retrieveTeam(team -> {
         String playerName;
         while (!(playerName = con.readLine("Enter player's name (to stop adding, type \"q\"): ")).equals("q")) {
            team.addPlayer(playerName);
            String country = con.readLine("Enter player's country: ");
            players.put(playerName, country);
         }
         teams.put(team.getName(), team);
      });
   }

   public void removePlayer() {
      retrieveTeam(team -> {
         String playerName = con.readLine("Enter player's name: ");
         team.removePlayer(playerName);
         teams.replace(team.getName(), team);
      });
   }

   public void removeTeam() {
      retrieveTeam(team -> {
         teams.remove(team.getName());
      });
   }

   public void printTeams() {
      CloseableIterator<Team> iterator = teams.values().iterator();
      if (!iterator.hasNext()) {
         con.printf("There are no teams \n");
         return;
      }

      while (iterator.hasNext()) {
         con.printf(iterator.next().toString());
      }
   }

   public void printPlayersAndCountry() {
      CloseableIterator<Map.Entry<String, String>> iterator = players.entrySet().iterator();
      if (!iterator.hasNext()) {
         con.printf("There are no players \n");
         return;
      }
      while (iterator.hasNext()) {
         Map.Entry<String, String> entry = iterator.next();
         con.printf("Player %s is from %s\n", entry.getKey(), entry.getValue());
      }
   }

   public void stop() {
      cacheManager.stop();
   }

   private void retrieveTeam(Consumer<Team> consumeTeam) {
      String teamName = con.readLine(msgEnterTeamName);
      try {
         tm.begin();
         Team team = teams.get(teamName);
         if (team == null) {
            con.printf(msgTeamMissing, teamName);
            tm.setRollbackOnly();
         } else {
            consumeTeam.accept(team);
         }
      } catch (Exception ex) {
         markForRollback(ex);
      } finally {
         endTransaction();
      }
   }

   private void markForRollback(Exception ex) {
      con.printf("Exception message %s", ex.getMessage());
      try {
         tm.setRollbackOnly();
      } catch (Exception exRollback) {
         con.printf("Transaction can't be marked for rollback after an error %s", exRollback.getMessage());
      }
   }

   private void endTransaction() {
      try {
         if (tm.getStatus() == Status.STATUS_ACTIVE) {
            tm.commit();
         } else {
            tm.rollback();
         }
      } catch (Exception e) {
         con.printf("Transaction can't be ended %s", e.getMessage());
      }
   }

   public static void main(String[] args) {
      Console con = System.console();
      FootballManager manager = new FootballManager();
      con.printf(initialPrompt);
      boolean running = true;
      while (running) {
         String action = con.readLine(">");
         switch (action) {
            case "at":
               manager.addTeam();
               break;
            case "ap":
               manager.addPlayers();
               break;
            case "rt":
               manager.removeTeam();
               break;
            case "rp":
               manager.removePlayer();
               break;
            case "p":
               manager.printTeams();
               break;
            case "pc":
               manager.printPlayersAndCountry();
               break;
            case "q":
               manager.stop();
               running = false;
               break;

            default:
               con.printf("Action not available \n");
               con.printf(initialPrompt);
         }
      }
   }

   public static String jdgProperty(String name) {
      Properties props = new Properties();
      try {
         props.load(FootballManager.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE));
      } catch (IOException ioe) {
         throw new RuntimeException(ioe);
      }
      return props.getProperty(name);
   }
}