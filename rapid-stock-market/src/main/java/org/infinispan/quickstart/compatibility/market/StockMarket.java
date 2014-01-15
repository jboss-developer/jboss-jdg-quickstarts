package org.infinispan.quickstart.compatibility.market;


import java.util.ArrayList;
import java.util.List;

public class StockMarket {

   public static void main(String args[]) throws Exception {

      final List<MarketUpdater> updaters = new ArrayList<MarketUpdater>();
      updaters.add(new MarketUpdater("NYSE:RHT", 50.0f));
      updaters.add(new MarketUpdater("NYSE:ENC", 30.0f));

      Runtime.getRuntime().addShutdownHook(new Thread() {
         public void run() {
            MarketUpdater.keepUpdating = false;
            for (MarketUpdater updater: updaters) {
               try {
                  updater.join(); //wait for the threads to properly close the cache manager
               } catch (InterruptedException e) {
                  e.printStackTrace();
               }
            }
         }
      });

      for (MarketUpdater updater: updaters) {
         updater.start();
      }
   }

}
