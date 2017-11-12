package elgrupo.elartefacto

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.ml.linalg.Matrix
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.ml.stat.Correlation
import org.apache.spark.sql.Row

/**
 * Sample Spark Scala application.
 *
 * For Windows computers, set HADOOP_HOME to the folder that contains winutils.exe (%HADOOP_HOME%/bin/winutils.exe)
 *
 * Steps for running it:
 * mvn package
 * spark-submit --class ${groupId}.${artifactId}.DescriptiveStatisticsApp ${artifactId}-${version}-jar-with-dependencies.jar
 *
 * @author ${user.name}
 */
object DescriptiveStatisticsApp {

  def main(args: Array[String]) {
    println("Hello World MLLib Spark!")

    val data = Seq(
      Vectors.sparse(4, Seq((0, 1.0), (3, -2.0))),
      Vectors.dense(4.0, 5.0, 0.0, 3.0),
      Vectors.dense(6.0, 7.0, 0.0, 8.0),
      Vectors.sparse(4, Seq((0, 9.0), (3, 1.0))))

    // create Spark context with Spark configuration
    val sc = new SparkContext(new SparkConf().setAppName("Spark Count"))

    val sqlContext = new org.apache.spark.sql.SQLContext(sc)
    import sqlContext.implicits._ 

    val df = data.map(Tuple1.apply).toDF("features")
    val Row(coeff1: Matrix) = Correlation.corr(df, "features").head
    println("Pearson correlation matrix:\n" + coeff1.toString)

    val Row(coeff2: Matrix) = Correlation.corr(df, "features", "spearman").head
    println("Spearman correlation matrix:\n" + coeff2.toString)
  }
}