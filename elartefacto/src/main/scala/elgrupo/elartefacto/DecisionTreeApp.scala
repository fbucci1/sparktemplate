package elgrupo.elartefacto

import org.apache.spark._
import org.apache.spark.rdd.RDD
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.tree.DecisionTree
import org.apache.spark.mllib.tree.model.DecisionTreeModel
import org.apache.spark.mllib.util.MLUtils
/**
 * Sample Spark Scala application.
 *
 * For Windows computers, set HADOOP_HOME to the folder that contains winutils.exe (%HADOOP_HOME%/bin/winutils.exe)
 *
 * Steps for running it:
 * mvn package
 * spark-submit --class ${groupId}.${artifactId}.DecisionTreeApp ${artifactId}-${version}-jar-with-dependencies.jar
 *
 * @author ${user.name}
 */
object DecisionTreeApp {

  case class Flight(dofM: String, dofW: String, carrier: String, tailnum: String, flnum: Int, org_id: String, origin: String, dest_id: String, dest: String, crsdeptime: Double, deptime: Double, depdelaymins: Double, crsarrtime: Double, arrtime: Double, arrdelay: Double, crselapsedtime: Double, dist: Int)

  def parseFlight(str: String): Flight = {
    val line = str.split(",")
    Flight(line(0), line(1), line(2), line(3), line(4).toInt, line(5), line(6), line(7), line(8), line(9).toDouble, line(10).toDouble, line(11).toDouble, line(12).toDouble, line(13).toDouble, line(14).toDouble, line(15).toDouble, line(16).toInt)
  }

  def main(args: Array[String]) {
    println("Hello World MLLib Spark!")

    // create Spark context with Spark configuration
    // TODO .setMaster("local[*]") should only be set for unit testing
    val sc = new SparkContext(new SparkConf().setAppName("Spark Sample App").setMaster("local[*]"));

    //Load and Parse the Data from a csv File
    val textRDD = sc.textFile("data/rita2014jan.csv")
    val flightsRDD = textRDD.map(parseFlight).cache()
    println(flightsRDD.first())

    //Extract Features
    var carrierMap: Map[String, Int] = Map()
    var index: Int = 0
    flightsRDD.map(flight => flight.carrier).distinct.collect.foreach(x => { carrierMap += (x -> index); index += 1 })
    println(carrierMap.toString)

    var originMap: Map[String, Int] = Map()
    var index1: Int = 0
    flightsRDD.map(flight => flight.origin).distinct.collect.foreach(x => { originMap += (x -> index1); index1 += 1 })
    println(originMap.toString)

    var destMap: Map[String, Int] = Map()
    var index2: Int = 0
    flightsRDD.map(flight => flight.dest).distinct.collect.foreach(x => { destMap += (x -> index2); index2 += 1 })
    println(destMap.toString)

    //Define Features Array
    val mlprep = flightsRDD.map(flight => {
      val monthday = flight.dofM.toInt - 1 // category
      val weekday = flight.dofW.toInt - 1 // category
      val crsdeptime1 = flight.crsdeptime.toInt
      val crsarrtime1 = flight.crsarrtime.toInt
      val carrier1 = carrierMap(flight.carrier) // category
      val crselapsedtime1 = flight.crselapsedtime.toDouble
      val origin1 = originMap(flight.origin) // category
      val dest1 = destMap(flight.dest) // category
      val delayed = if (flight.depdelaymins.toDouble > 40) 1.0 else 0.0
      Array(delayed.toDouble, monthday.toDouble, weekday.toDouble, crsdeptime1.toDouble, crsarrtime1.toDouble, carrier1.toDouble, crselapsedtime1.toDouble, origin1.toDouble, dest1.toDouble)
    })
    println(mlprep.take(1))

    //create labeled points
    val mldata = mlprep.map(x => LabeledPoint(x(0), Vectors.dense(x(1), x(2), x(3), x(4), x(5), x(6), x(7), x(8))))
    println(mldata.take(1))

    val mldata0 = mldata.filter(x => x.label == 0).randomSplit(Array(0.85, 0.15))(1)
    val mldata1 = mldata.filter(x => x.label != 0)
    val mldata2 = mldata0 ++ mldata1
    val splits = mldata2.randomSplit(Array(0.7, 0.3))
    val (trainingData, testData) = (splits(0), splits(1))
    println(testData.take(1))

    //Train the Model
    var categoricalFeaturesInfo = Map[Int, Int]()
    categoricalFeaturesInfo += (0 -> 31)
    categoricalFeaturesInfo += (1 -> 7)
    categoricalFeaturesInfo += (4 -> carrierMap.size)
    categoricalFeaturesInfo += (6 -> originMap.size)
    categoricalFeaturesInfo += (7 -> destMap.size)
    val numClasses = 2
    val impurity = "gini"
    val maxDepth = 9
    val maxBins = 7000
    val model = DecisionTree.trainClassifier(trainingData, numClasses, categoricalFeaturesInfo,
      impurity, maxDepth, maxBins)
    println(model.toDebugString)

    //Test the model
    val labelAndPreds = testData.map { point =>
      val prediction = model.predict(point.features)
      (point.label, prediction)
    }
    labelAndPreds.take(3)
    val wrongPrediction = (labelAndPreds.filter {
      case (label, prediction) => (label != prediction)
    })
    println(wrongPrediction.count())
    val ratioWrong = wrongPrediction.count().toDouble / testData.count()
    println(ratioWrong)

  }
}