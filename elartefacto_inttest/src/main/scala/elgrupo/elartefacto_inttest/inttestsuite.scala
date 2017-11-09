package elgrupo.elartefacto_inttest

import org.junit.runner.JUnitCore
import scala.collection.JavaConversions._

object TestRunner {

  def main(args: Array[String]) {
    //
    var testClasses: Array[Class[_]] = Array(classOf[IntTestCases]);
    //
    var anyFailure = executeTests(testClasses);
    //
    System.exit(if (anyFailure) 1 else 0);
    //
  }

  def executeTests(testClasses: Array[Class[_]]): Boolean = {
    //
    println("-------------------------------------------------------")
    println(" I N T E G R A T I O N   T E S T S")
    println("-------------------------------------------------------")
    println("")
    //
    var jUnitCore: JUnitCore = new JUnitCore();
    //
    var anyFailure = false;
    //
    for (testClass <- testClasses) {
      //
      println(s"Executing tests in ${testClass}");
      //
      var result = jUnitCore.run(testClass.asInstanceOf[Class[_]]);
      //
      var failures = result.getFailures.toList;
      for (failure <- failures) {
        println(s"* Failure: ${failure}.");
        anyFailure = true;
      }
      //
      println("")
      println("Results :")
      println("")
      println(s"Tests run: ${result.getRunCount}, Failures: ${result.getFailureCount}, Skipped: ${result.getIgnoreCount}");
      println("")
      //
    }
    //
    return anyFailure;
    //
  }
}
