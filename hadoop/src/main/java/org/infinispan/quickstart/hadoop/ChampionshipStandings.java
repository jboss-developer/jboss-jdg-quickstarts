package org.infinispan.quickstart.hadoop;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.operators.Order;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.aggregation.Aggregations;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Job;
import org.infinispan.hadoop.InfinispanConfiguration;
import org.infinispan.hadoop.InfinispanInputFormat;

import java.util.List;

/**
 * Given a set of {@link MatchResult}, will calculate the classification table containing points won per team.
 *
 * @author gustavonalle
 */
public class ChampionshipStandings {

   public static final int POINTS_PER_WIN = 3;
   public static final int POINTS_PER_DRAW = 1;

   public static void main(String[] args) throws Exception {

      // Configure the Infinispan InputFormat wrapping it in a Hadoop Job class.
      Configuration configuration = new Configuration();
      configuration.set(InfinispanConfiguration.INPUT_REMOTE_CACHE_HOST, "localhost");
      configuration.set(InfinispanConfiguration.OUTPUT_REMOTE_CACHE_NAME, "default");
      Job job = Job.getInstance(configuration, "La Liga Standings");
      InfinispanInputFormat<Integer, MatchResult> infinispanInputFormat = new InfinispanInputFormat<>();

      // Obtain the Execution environment from Flink
      final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

      // Create a DataSource that reads data using the InfinispanInputFormat
      DataSource<Tuple2<Integer, MatchResult>> infinispanDS = env.createHadoopInput(infinispanInputFormat, Integer.class, MatchResult.class, job);

      // For each entry (K,V) in the cache, extracts the value, calculate points per team, emmit tuples (Team, point),
      // group by team summing the points and finally sort results by points won.
      List<Tuple2<String, Integer>> results = infinispanDS.flatMap(new FlatMapFunction<Tuple2<Integer, MatchResult>, Tuple2<String, Integer>>() {
         @Override
         public void flatMap(Tuple2<Integer, MatchResult> entry, Collector<Tuple2<String, Integer>> collector) throws Exception {
            int homePoints = 0;
            int awayPoints = 0;
            MatchResult match = entry.f1;
            if (match.getHomeGoals() == match.getAwayGoals()) {
               homePoints = awayPoints = POINTS_PER_DRAW;
            } else if (match.getHomeGoals() > match.getAwayGoals()) {
               homePoints = POINTS_PER_WIN;
            } else {
               awayPoints = POINTS_PER_WIN;
            }
            collector.collect(new Tuple2<>(match.getHomeTeam(), homePoints));
            collector.collect(new Tuple2<>(match.getAwayTeam(), awayPoints));
         }
      }).groupBy(0).aggregate(Aggregations.SUM, 1).sortPartition(1, Order.DESCENDING).collect();

      // Prints the result
      String format = "%-20s%s%n";
      System.out.printf("\n\n*** La Liga Standings ***\n\n");
      System.out.printf(format, "Team", "Points");
      System.out.printf("----------------------------\n");
      results.forEach(t -> System.out.printf(format, t.f0, t.f1));
   }

}
