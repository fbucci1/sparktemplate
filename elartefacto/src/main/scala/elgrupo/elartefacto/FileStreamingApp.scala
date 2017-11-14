package elgrupo.elartefacto

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.ml.linalg.Matrix
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.ml.stat.Correlation
import org.apache.spark.sql.Row
import org.apache.spark.streaming.flume._
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.DStream
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.LongWritable
import org.apache.hadoop.io.Text
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat
import org.apache.spark.rdd.RDD

/**
 * Sample Spark Scala application.
 *
 * For Windows computers, set HADOOP_HOME to the folder that contains winutils.exe (%HADOOP_HOME%/bin/winutils.exe)
 *
 * Steps for running it:
 * mvn package
 * spark-submit --class ${groupId}.${artifactId}.FileStreamingApp ${artifactId}-${version}-jar-with-dependencies.jar
 *
 * @author ${user.name}
 */
object FileStreamingApp {

  def main(args: Array[String]) {
    println("Hello World Flume Spark Streaming!")

    // create Spark context with Spark configuration
    val sc = new SparkContext(new SparkConf().setAppName("FlumeStreamingApp").setMaster("local[*]"))

    val ssc = new StreamingContext(sc, Seconds(5))

    //This reads content from Flume sink: MyAgent.sinks.sin-mysink.type = avro
    val stream = FlumeUtils.createStream(ssc, "localhost", 4444)
    val lines = stream.map {
      e => new String(e.event.getBody().array(), "UTF-8")
    }
    val words = lines.flatMap(_.split(" "))
    val wordCounts = words.map(x => (x, 1))
    wordCounts.print()

    ssc.start()
    ssc.awaitTermination()
  }
}