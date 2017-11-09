package elgrupo.elartefacto_inttest

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import scala.collection.JavaConversions._;

object TestRunner {

  def main(args: Array[String]) {
    var result = JUnitCore.runClasses(classOf[TestCases]);
    var failures = result.getFailures.toList;
    for (failure <- failures) {
      System.out.println(failure.toString());
    }
    System.out.println(result.wasSuccessful());
  }
}  