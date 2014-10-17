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

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.exceptions.HotRodClientException;

import javax.security.sasl.SaslException;
import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Martin Gencur
 * @author Vitalii Chepeliuk
 */
public class FootballManager {

    private static final String JDG_HOST = "jdg.host";
    private static final String HOTROD_PORT = "jdg.hotrod.port";
    private static final String PROPERTIES_FILE = "jdg.properties";

    private static final String TEAM_MISSING_MSG = "The specified team \"%s\" does not exist, choose next operation\n";
    private static final String ENTER_TEAM_NAME_MSG = "Enter team name: ";
    private static final String ENTER_PLAYERS_NAME_MSG = "Enter player's name: ";
    private static final String ENTER_PLAYERS_TEAM_MSG = "Enter player's team: ";

    private static final String INITIAL_PROMPT = AnsiColors.GREEN.color()
            + "Choose action:\n"
            + "============= \n"
            + "at  -  add a team\n"
            + "ap  -  add a player to a team\n"
            + "rt  -  remove a team\n"
            + "rp  -  remove a player from a team\n"
            + "p   -  print all teams and players\n"
            + "q   -  quit"
            + AnsiColors.END.color() + "\n";
    private static final String LOGIN_PROMPT = AnsiColors.YELLOW.color()
            + "1. Enter username and password to operate with cache\n"
            + "2. Exit"
            + AnsiColors.END.color() + "\n";

    private static final String teamsKey = "teams";
    private static final String REALM = "ApplicationRealm";
    private static final String SERVER_NAME = "football";

    private Console con;
    private RemoteCacheManager cacheManager;
    private RemoteCache<String, Object> cache;


    public FootballManager(Console con, String login, char[] password) {
        this.con = con;
        cacheManager = new RemoteCacheManager(getRemoteCacheManagerConfig(login, password));
        cache = cacheManager.getCache("teams");
    }

    public void addTeam() {
        String teamName = con.readLine(ENTER_TEAM_NAME_MSG);
        List<String> teams = (List<String>) cache.get(teamsKey);
        if (teams == null) {
            teams = new ArrayList<String>();
        }
        Team t = new Team(teamName);
        cache.put(teamName, t);
        teams.add(teamName);
        // maintain a list of teams under common key
        cache.put(teamsKey, teams);
    }

    public void addPlayers() {
        String teamName = con.readLine(ENTER_TEAM_NAME_MSG);
        String playerName;
        Team t = (Team) cache.get(teamName);
        if (t != null) {
            while (!(playerName = con.readLine("Enter player's name" + "(to stop adding, type \"q\"): ")).equals("q")) {
                t.addPlayer(playerName);
            }
            cache.put(teamName, t);
        } else {
            con.printf(TEAM_MISSING_MSG, teamName);
        }
    }

    public void removePlayer() {
        String playerName = con.readLine(ENTER_PLAYERS_NAME_MSG);
        String teamName = con.readLine(ENTER_PLAYERS_TEAM_MSG);
        Team t = (Team) cache.get(teamName);
        if (t != null) {
            t.removePlayer(playerName);
            cache.put(teamName, t);
        } else {
            con.printf(TEAM_MISSING_MSG, teamName);
        }
    }

    public void removeTeam() {
        String teamName = con.readLine(ENTER_TEAM_NAME_MSG);
        Team t = (Team) cache.get(teamName);
        if (t != null) {
            cache.remove(teamName);
            List<String> teams = (List<String>) cache.get(teamsKey);
            if (teams != null) {
                teams.remove(teamName);
            }
            cache.put(teamsKey, teams);
        } else {
            con.printf(TEAM_MISSING_MSG, teamName);
        }
    }

    public void printTeams() {
        List<String> teams = (List<String>) cache.get(teamsKey);
        if (teams != null) {
            for (String teamName : teams) {
                con.printf(cache.get(teamName).toString());
            }
        }
    }

    public void stop() {
        cacheManager.stop();
    }

    public static void main(String[] args) {
        Console con = System.console();
        while (true) {
            try {
                con.printf(LOGIN_PROMPT);
                String action = con.readLine(">");
                if (action.equals("1")) {
                    String login = con.readLine(AnsiColors.CYAN.color() + "Enter username: " + AnsiColors.END.color());
                    char[] password = con.readPassword(AnsiColors.CYAN.color() + "Enter password: " + AnsiColors.END.color());
                    FootballManager manager = new FootballManager(con, login, password);
                    footballAction(con, manager);
                } else if (action.equals("2")) {
                    break;
                }
            } catch (HotRodClientException ex) {
                if (isCause(SaslException.class, ex))
                    con.printf(AnsiColors.RED.color() + "ACCESS DENIED, WRONG CREDENTIALS" + AnsiColors.END.color() + "\n");
                else
                    con.printf(AnsiColors.RED.color() + "ACCESS DENIED, CONNECTION REFUSED" + AnsiColors.END.color() + "\n");
            }
        }
    }

    private static void footballAction(Console con, FootballManager manager) {
        con.printf(INITIAL_PROMPT);
        String action = null;
        while (true) {
            try {
                action = con.readLine(">");
                if ("at".equals(action)) {
                    manager.addTeam();
                } else if ("ap".equals(action)) {
                    manager.addPlayers();
                } else if ("rt".equals(action)) {
                    manager.removeTeam();
                } else if ("rp".equals(action)) {
                    manager.removePlayer();
                } else if ("p".equals(action)) {
                    manager.printTeams();
                } else if ("q".equals(action)) {
                    manager.stop();
                    break;
                }
            } catch (HotRodClientException ex) {
                con.printf(AnsiColors.RED.color() + "ACCESS DENIED: %s, PERMISSION RESTRICTED" + AnsiColors.END.color() + "\n", action);
            }
        }
    }

    protected Configuration getRemoteCacheManagerConfig(String login, char[] password) {
        ConfigurationBuilder config = new ConfigurationBuilder();
        // add server host and server port we are connecting to
        config.addServer().host(jdgProperty(JDG_HOST)).port(Integer.parseInt(jdgProperty(HOTROD_PORT)));
        // add configuration for authentication
       config.security().authentication()
                .serverName(SERVER_NAME) //define server name, should be specified in XML configuration
                .saslMechanism("DIGEST-MD5") // define SASL mechanism, in this example we use DIGEST with MD5 hash
                .callbackHandler(new LoginHandler(login, password, REALM)) // define login handler, implementation defined
                .enable();

        return config.build();
    }

    public String jdgProperty(String name) {
        Properties props = new Properties();
        try {
            props.load(getClass().getResourceAsStream("/" + PROPERTIES_FILE));
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        return props.getProperty(name);
    }

    public static boolean isCause(Class<? extends Throwable> expected, Throwable ex) {
        return ex.getCause().getMessage().contains(expected.getName());
    }
}