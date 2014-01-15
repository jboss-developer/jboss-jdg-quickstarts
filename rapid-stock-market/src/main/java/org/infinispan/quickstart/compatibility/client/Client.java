package org.infinispan.quickstart.compatibility.client;

import org.infinispan.quickstart.compatibility.commands.Command;
import org.infinispan.quickstart.compatibility.commands.HistoryCommand;
import org.infinispan.quickstart.compatibility.commands.PeekSharesCommand;

import java.io.Console;
import java.util.HashMap;
import java.util.Map;

/**
 * An entry point to the client-side application. Periodically checks for next requested action
 * and delegates the execution to a pre-defined set of commands.
 *
 * @author Martin Gencur
 */
public class Client {

   private static String PROMPT =  "Choose action, e.g.:\n" + "=================== \n"
         + "peek NYSE:RHT            -  Check the latest value of NYSE:RHT shares\n"
         + "history NYSE:ENC 10m 10s -  Show the history of NYSE:ENC shares for last 10 minutes with 10-second step (other options XXm (XX minutes), XXh (XX hours))\n"
         + "quit                     -  Quit the application\n";

   private final Console CONSOLE;

   private Map<String, Command> commands;

   public Client(Console console) {
      this.CONSOLE = console;
      commands = new HashMap<String, Command>();
      commands.put("peek", new PeekSharesCommand());
      commands.put("history", new HistoryCommand());
   }

   public static void main(String args[]) throws Exception {
      Console con = System.console();
      Client client = new Client(con);
      con.printf(PROMPT);

      while (true) {
         String input = con.readLine(">");
         if ("quit".equals(input)) break;
         Command cmd = client.parseCommand(input);
         if (cmd == null) {
            con.printf("Unable to perform the requested action.\n");
         } else {
            cmd.execute(con, input);
         }
      }
   }

   private Command parseCommand(String input) {
      String commandName = input.split("\\s")[0];
      if (commandName != null && commands.keySet().contains(commandName)) {
         return commands.get(commandName);
      } else {
         return null;
      }
   }

}
