package org.infinispan.quickstart.compatibility.commands;


import org.infinispan.quickstart.compatibility.common.SharesUpdate;

import java.io.Console;

/**
 * Checks the latest value of shares. Only one value is retrieved via HTTP client.
 * Prints the result to the console.
 *
 * @author Martin Gencur
 */
public class PeekSharesCommand extends AbstractRestCommand {

   @Override
   public int getNumArgs() {
      return 1;
   }

   @Override
   public void execute(Console console, String argLine) {
      String[] args = argLine.split("\\s");
      if (args.length != getNumArgs() + 1) {
         console.printf("The argument list is incorrect\n");
         return;
      }

      String nameOfShares = args[1];
      try {
         SharesUpdate sharesUpdate = (SharesUpdate) getSharesUpdateViaRest(composeKey(nameOfShares, "last"));
         if (sharesUpdate != null) {
            console.printf("------------- %s -------------\n", nameOfShares);
            console.printf("Date:  %s\n", sharesUpdate.getDate().toString());
            console.printf("Value: %f USD\n", sharesUpdate.getValue());
         } else {
            console.printf("Last stock value for " + nameOfShares + " not found.\n");
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

}
