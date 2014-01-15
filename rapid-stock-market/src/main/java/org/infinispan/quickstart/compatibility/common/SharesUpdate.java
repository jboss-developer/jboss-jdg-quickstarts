package org.infinispan.quickstart.compatibility.common;

import java.io.Serializable;
import java.util.Date;


/**
 * Holds the value of shares and is used as a value in key-value mappings which are
 * stored in a cache via HotRod protocol and retrieved via REST.
 *
 * This class must be available to both clients (HotRod client, REST client) and also to the
 * server so that it can be marshalled/unmarhalled and stored in the cache. Hence,
 * this class must be packaged in a jar file and installed into JDG server as a special module.
 *
 * @author Martin Gencur
 */
public class SharesUpdate implements Serializable {

   private static final long serialVersionUID = 6529685098267757690L;

   private float value;

   private String stockId;

   private Date date;

   public SharesUpdate(Date date, String stockId, float value) {
      this.value = value;
      this.stockId = stockId;
      this.date = date;
   }

   public float getValue() {
      return value;
   }

   public String getStockId() {
      return stockId;
   }

   public Date getDate() {
      return date;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      SharesUpdate that = (SharesUpdate) o;

      if (Float.compare(that.value, value) != 0) return false;
      if (!stockId.equals(that.stockId)) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = (value != +0.0f ? Float.floatToIntBits(value) : 0);
      result = 31 * result + stockId.hashCode();
      return result;
   }
}
