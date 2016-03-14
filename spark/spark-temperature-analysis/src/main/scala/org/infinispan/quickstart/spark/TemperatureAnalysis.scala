package org.infinispan.quickstart.spark

import java.util.Properties

import org.apache.spark.SparkConf
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.dstream.DStream.toPairDStreamFunctions
import org.apache.spark.streaming.dstream.{DStream, MapWithStateDStream}
import org.apache.spark.streaming.{Seconds, State, StateSpec, StreamingContext}
import org.infinispan.spark.stream.{InfinispanDStream, InfinispanInputDStream}

/**
 * <p>Computes average temperature in each place from incoming stream of measurements.</p>
 * <p>The measurement stream is read from the Data Grid and produces stream of avg. temperatures.
 * Updates are written back to the Data Grid into a different cache called avg-temperatures.
 */
object TemperatureAnalysis {

   val dataGridServer = "127.0.0.1:11222"
   val inputDataGridCache = "default"
   val outputDataGridCache = "avg-temperatures"

   /**
    * <p>
    * Computes latest average temperature for given place and updates the state.
    * </p>
    * <p>
    * As we want to update average temperature every time new measurement arrives, we need to keep all previous
    * measurement somewhere. We can take advantage of Spark [[org.apache.spark.streaming.State]] and store the
    * sum of temperatures and number of measurements for each place.
    * Once a new measurement arrives, we update the sums, store them in state and compute new average temperature.
    * </p>
    */
   val mapFunc = (place: String, temps: Option[Iterable[Double]], state: State[Map[String, (Double, Long)]]) => {
      // obtain the state, a map contained the numbers of measurements and the running average for each place
      val stateMap: Map[String, (Double, Long)] = state.getOption().getOrElse(Map[String, (Double, Long)]())

      // obtain the running average and number of measurements for the place
      val sums: (Double, Long) = stateMap.getOrElse(place, (0d, 0L))

      // update
      val curTemps: Iterable[Double] = temps.getOrElse(List())
      val sumUp = sums._1 + curTemps.sum
      val countUp = sums._2 + curTemps.size

      // update stored state
      state.update(stateMap.updated(place, (sumUp, countUp)))
      // emmit the new average
      (place, sumUp / countUp)
   }

   def main(args: Array[String]) {

      // Initialize the Spark streaming context, with a batch duration of 1 second.
      val sparkConf = new SparkConf().setAppName("TemperatureAnalysis")
      val ssc = new StreamingContext(sparkConf, Seconds(1))

      // set up checkpoint to store state information
      ssc.checkpoint("/tmp/spark-temperature")

      // configure the connection to the DataGrid and create the incoming DStream
      val configIn = new Properties
      configIn.put("infinispan.rdd.cacheName", inputDataGridCache)
      configIn.put("infinispan.client.hotrod.server_list", dataGridServer)
      val ispnStream = new InfinispanInputDStream[String, Double](ssc, StorageLevel.MEMORY_ONLY, configIn)

      // extract the (place, temperature) pair from the incoming stream that is composed of (key, value, eventType)
      // we are assuming only events of type CLIENT_CACHE_ENTRY_CREATED will be present.
      val measurementStream: DStream[(String, Double)] = ispnStream map { case (key, value, eventType) => (key, value) }

      // as measurements are batched, it can contain several measurements from the same place - let's group them together
      val measurementGrouped: DStream[(String, Iterable[Double])] = measurementStream.groupByKey()

      // Produce a new DStream combining the previous DStream with the stored state
      val avgTemperatures: MapWithStateDStream[String, Iterable[Double], Map[String, (Double, Long)], (String, Double)] =
         measurementGrouped.mapWithState(StateSpec.function(mapFunc))

      // just print number of items in each RDD
      avgTemperatures.foreachRDD(rdd => {
         printf("# items in DStream: %d\n", rdd.count())
         rdd.foreach { case (place, average) => println("Averages:" + place + " -> " + average) }
      })

      // write average temperature stream to the avg-temperatures cache
      val configOut = new Properties
      configOut.put("infinispan.rdd.cacheName", outputDataGridCache)
      configOut.put("infinispan.client.hotrod.server_list", dataGridServer)
      avgTemperatures.writeToInfinispan(configOut)

      ssc.start()
      ssc.awaitTermination()
   }
}