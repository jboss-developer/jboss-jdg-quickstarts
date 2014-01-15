package org.infinispan.quickstart.compatibility.commands;


import org.infinispan.quickstart.compatibility.common.SharesUpdate;

import java.io.Console;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Checks the requested history of shares. First, parses arguments specifying the length of history and step.
 * Second, generates a proper key set. Third, retrieves share updates stored under the key set and prints the history.
 *
 * @author Martin Gencur
 */
public class HistoryCommand extends AbstractRestCommand {

   private Calendar cal = new GregorianCalendar();

   @Override
   public int getNumArgs() {
      return 3;
   }

   @Override
   public void execute(Console console, String argLine) {
      String[] args = argLine.split("\\s");
      String stockName = args[1];
      String maxHistoryArg = args[2];
      String stepArg = args[3];

      if (args.length != getNumArgs() + 1) {
         console.printf("The argument list is incorrect\n");
         return;
      }

      try {
         //get last existing update and its date
         SharesUpdate lastUpdate = (SharesUpdate) getSharesUpdateViaRest(composeKey(stockName, "last"));
         //get all updates between last existing update and requested date in history
         List<String> stockHistoryKeys = getStockHistoryKeySet(stockName, lastUpdate.getDate(), toSeconds(maxHistoryArg), toSeconds(stepArg));

         float previous = 0.0f, current = 0.0f;

         console.printf("------------- History of %s -------------\n", stockName);
         for (String key : stockHistoryKeys) {
            SharesUpdate sharesUpdate = (SharesUpdate) getSharesUpdateViaRest(key);
            if (sharesUpdate != null) {
               current = sharesUpdate.getValue();
               console.printf("Date:  %s, value: %f USD (%s %+f%%)\n",
                              sharesUpdate.getDate().toString(),
                              current,
                              current > previous ? "increase" : "decrease",
                              computeIncreaseInPercent(previous, current));
            }
            previous = current;
         }

      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   private float computeIncreaseInPercent(float previous, float current) {
      if (previous == 0.0f) {
         return 0.0f;
      } else {
         return (current / (previous / 100)) - 100.0f;
      }
   }

   /*
    * Convert numbers like "2m" or "10h" to the number of seconds
    */
   private long toSeconds(String duration) {
      Pattern regex = Pattern.compile("(\\d+)([smh])");  //an integer followed by 's','m','h'
      Matcher regexMatcher = regex.matcher(duration);
      long numSeconds = 1;
      if (regexMatcher.matches()) {
         int amount = Integer.parseInt(regexMatcher.group(1)); //get the number
         Character unitChar = regexMatcher.group(2).charAt(0); //get the unit character
         switch (unitChar) {
            case 's':
               numSeconds = TimeUnit.SECONDS.toSeconds(amount);
               break;
            case 'm':
               numSeconds = TimeUnit.MINUTES.toSeconds(amount);
               break;
            case 'h':
               numSeconds = TimeUnit.HOURS.toSeconds(amount);
               break;
         }
      }
      return numSeconds;
   }


   private List<String> getStockHistoryKeySet(String stockName, Date lastUpdateTime, long seconds, long step) {
      List<String> keys = new ArrayList<String>();
      cal.setTime(lastUpdateTime); //start from latest valid update
      long counter = 0;
      while (counter <= seconds) {
         keys.add(composeKey(stockName, KEY_DATE_FORMAT.format(cal.getTime())));
         cal.add(Calendar.SECOND, (int) -step);
         counter += step;
      }
      Collections.reverse(keys); //get the keys in ascending order
      return Collections.unmodifiableList(keys);
   }

}
