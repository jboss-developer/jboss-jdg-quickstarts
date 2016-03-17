package org.infinispan.quickstart.hadoop;

import java.io.Serializable;

/**
 * A football match result.
 *
 * @author gustavonalle
 */
public class MatchResult implements Serializable {

   private final int id;
   private final String homeTeam;
   private final String awayTeam;
   private final int homeGoals;
   private final int awayGoals;

   public MatchResult(int id, String homeTeam, String awayTeam, int homeGoals, int awayGoals) {
      this.id = id;
      this.homeTeam = homeTeam;
      this.awayTeam = awayTeam;
      this.homeGoals = homeGoals;
      this.awayGoals = awayGoals;
   }

   public String getHomeTeam() {
      return homeTeam;
   }

   public String getAwayTeam() {
      return awayTeam;
   }

   public int getHomeGoals() {
      return homeGoals;
   }

   public int getAwayGoals() {
      return awayGoals;
   }

   public int getId() {
      return id;
   }

   @Override
   public String toString() {
      return homeTeam + " " + homeGoals + ":" + awayGoals + " " + awayTeam;
   }
}
