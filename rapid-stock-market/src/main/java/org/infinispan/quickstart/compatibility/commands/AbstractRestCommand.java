package org.infinispan.quickstart.compatibility.commands;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 * A common class for commands using REST client/protocol.
 *
 * @author Martin Gencur
 */
public abstract class AbstractRestCommand implements Command {

   protected final DateFormat KEY_DATE_FORMAT = new SimpleDateFormat("dd_MMM_yyyy_HH_mm_ss", Locale.US);

   private final String DEFAULT_REST_URL = "http://localhost:8080/rest/default";

   private HttpClient restClient = new HttpClient();

   public AbstractRestCommand() {
      KEY_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
   }

   protected Object getSharesUpdateViaRest(String key) throws IOException, ClassNotFoundException {
      HttpMethod get = new GetMethod(DEFAULT_REST_URL + "/" + key);
      get.setRequestHeader("Accept", "application/x-java-serialized-object");

      restClient.executeMethod(get);

      if (get.getStatusCode() == HttpStatus.SC_OK) {
         ObjectInputStream ois = new ObjectInputStream(get.getResponseBodyAsStream());
         return ois.readObject();
      } else {
         return null;
      }
   }

   /*
    Compose key in the form arg1_arg2_..._argN
    */
   protected String composeKey(String... args) {
      StringBuilder bld = new StringBuilder();
      for (String arg: args) {
         bld.append(arg);
         bld.append("_");
      }
      bld.deleteCharAt(bld.length() - 1); //remove last underscore
      return bld.toString();
   }

}
