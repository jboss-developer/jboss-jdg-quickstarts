package org.infinispan.quickstart.hadoop;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.impl.ConfigurationProperties;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import static java.util.Arrays.asList;

/**
 * Loads matches from the text file into the cache.
 *
 * @author gustavonalle
 */
public class DataLoader {

   static final String DATAGRID_IP = "127.0.0.1";
   static final String DATA_FILE = "matches.txt";
   static final String SEPARATOR = ",";

   public static void main(String[] args) throws Exception {
      // Configure remote cache
      ConfigurationBuilder builder = new ConfigurationBuilder();
      builder.addServer().host(DATAGRID_IP).port(ConfigurationProperties.DEFAULT_HOTROD_PORT);
      RemoteCacheManager cacheManager = new RemoteCacheManager(builder.build());
      RemoteCache<Integer, MatchResult> cache = cacheManager.getCache();

      // Read file containing matches and insert them in the default cache.
      InputStream resource = DataLoader.class.getClassLoader().getResourceAsStream(DATA_FILE);
      BufferedReader reader = new BufferedReader(new InputStreamReader(resource));

      reader.lines().parallel()
              .map(match -> asList(match.split(SEPARATOR)))
              .map(list -> {
                 Iterator<String> iter = list.iterator();
                 return new MatchResult(Integer.valueOf(iter.next()), iter.next(), iter.next(),
                         Integer.valueOf(iter.next()), Integer.valueOf(iter.next()));
              })
              .forEach(match -> cache.put(match.getId(), match));

      System.out.printf("Finished importing %d 'La Liga' matches.", cache.size());
      cacheManager.stop();
      reader.close();
      System.exit(0);
   }

}
