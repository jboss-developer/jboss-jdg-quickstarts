package org.infinispan.quickstart.compatibility.market;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.quickstart.compatibility.common.SharesUpdate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

/**
 * Periodically updates the remote cache (through HotRod client) with a new value of shares.
 *
 * @author Martin Gencur
 */
public class MarketUpdater extends Thread {

   static volatile boolean keepUpdating = true;

   private final String SERVER_HOST = "localhost";
   private final int SERVER_PORT = 11222;
   private final DateFormat KEY_DATE_FORMAT = new SimpleDateFormat("dd_MMM_yyyy_HH_mm_ss", Locale.US);
   private final int MAX_TREND_LENGTH = 15; //how long the value of a stock can grow or fall before next change in trend
   private final float RANDOM_VALUE_FACTOR = 0.5f;
   private final int UPDATE_SHARES_INTERVAL = 500;
   private final String NAME_OF_SHARES;
   private final String LAST_UPDATE_SUFFIX = "last";

   private SharesTrend sharesTrend;
   private Calendar cal = new GregorianCalendar();

   private RemoteCacheManager rcm;
   private RemoteCache<String, SharesUpdate> cache;

   public MarketUpdater(String stockName, float initialValue) {
      this.NAME_OF_SHARES = stockName;
      this.sharesTrend = new SharesTrend(stockName, initialValue);
      KEY_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));

      //configure the HotRod client and retrieve the cache
      Configuration config = new ConfigurationBuilder().addServer().host(SERVER_HOST).port(SERVER_PORT).build();
      rcm = new RemoteCacheManager(config);
      cache = rcm.getCache();
   }

   public void run() {
      System.out.println("Starting MarketUpdater for " + NAME_OF_SHARES + ". Press Ctrl-C to exit...");
      while (keepUpdating) {
         updateMarket();
         try {
            Thread.sleep(UPDATE_SHARES_INTERVAL);
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
      if (rcm != null) {
         System.out.println("Shutting down the cache manager...");
         rcm.stop();
      }
   }

   private void updateMarket() {
      float nextValue = nextValueOfShares();
      Date nextDate = getNextDate();
      String nextDateString = KEY_DATE_FORMAT.format(nextDate);

      SharesUpdate update = new SharesUpdate(nextDate, NAME_OF_SHARES, nextValue);
      //store an update in the cache
      cache.put(NAME_OF_SHARES + "_" + nextDateString, update);
      //also remember the last stored update
      cache.put(NAME_OF_SHARES + "_" + LAST_UPDATE_SUFFIX, update);
   }

   private Date getNextDate() {
      cal.add(Calendar.SECOND, 1);
      return cal.getTime();
   }

   private float nextValueOfShares() {
      if (sharesTrend.currentUpdateIndex == sharesTrend.nextChangeInTrend) {
         sharesTrend.isGrowingTrend = sharesTrend.RANDOM_GENERATOR.nextBoolean();
         sharesTrend.nextChangeInTrend += sharesTrend.RANDOM_GENERATOR.nextInt(MAX_TREND_LENGTH) + 1; //avoid 0
      }
      sharesTrend.currentUpdateIndex++;
      sharesTrend.valueOfShares += (sharesTrend.RANDOM_GENERATOR.nextFloat() * RANDOM_VALUE_FACTOR) * (sharesTrend.isGrowingTrend == false ? -1 : 1);
      return sharesTrend.valueOfShares;
   }

   /*
    * Being used to generate the course of shares.
    */
   class SharesTrend {

      final Random RANDOM_GENERATOR;

      float valueOfShares;

      int currentUpdateIndex;

      boolean isGrowingTrend; //specifies whether the value of shares is currently growing or not

      int nextChangeInTrend;

      SharesTrend(String nameOfShares, float valueOfShares) {
         this.valueOfShares = valueOfShares;
         this.RANDOM_GENERATOR = new Random(nameOfShares.hashCode());
      }

   }

}
