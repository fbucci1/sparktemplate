package elgrupo.elartefacto

import org.apache.hadoop.io.{ LongWritable, Text }
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat
import org.apache.spark.{ SparkContext, SparkConf }
import org.apache.spark.streaming.{ Seconds, StreamingContext }

/**
 * Sample Spark Scala application.
 *
 * For Windows computers, set HADOOP_HOME to the folder that contains winutils.exe (%HADOOP_HOME%/bin/winutils.exe)
 *
 * Steps for running it:
 * mvn package
 * spark-submit --class ${groupId}.${artifactId}.FlumeStreamingApp ${artifactId}-${version}-jar-with-dependencies.jar
 *
 * @author ${user.name}
 */
object FlumeStreamingApp {

  def main(args: Array[String]) {
    println("Hello World Flume Spark Streaming!")

    // create Spark context with Spark configuration
    val sc = new SparkContext(new SparkConf().setAppName("FlumeStreamingApp").setMaster("local[*]"))
    val ssc = new StreamingContext(sc, Seconds(5))

    val inputDirectory = "file:/tmp"

    val lines = ssc.fileStream[LongWritable, Text, TextInputFormat](inputDirectory).map { case (x, y) => (x.toString, y.toString) }

    lines.print()

    lines.foreachRDD( file => println(file, file.name, file.id, file.partitioner, file.count()) );
    
    ssc.start()
    ssc.awaitTermination()

  }

}