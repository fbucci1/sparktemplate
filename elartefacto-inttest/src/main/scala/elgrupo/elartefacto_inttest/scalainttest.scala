package elgrupo.elartefacto_inttest

import scala.collection.mutable.Stack

import org.junit.Test
import org.scalatest.Assertions
import elgrupo.elartefacto.SparkApp

class TestCases extends Assertions {

  @Test def callApp() { 
    SparkApp.main((Array[String]){"someParameter!"});
  }

  @Test def stackShouldPopValuesIinLastInFirstOutOrder() {
    val stack = new Stack[Int]
    stack.push(1)
    stack.push(2)
    assert(stack.pop() === 2)
    assert(stack.pop() === 1)
  }

  @Test def stackShouldThrowNoSuchElementExceptionIfAnEmptyStackIsPopped() {
    val emptyStack = new Stack[String]
    intercept[NoSuchElementException] {
      emptyStack.pop()
    }
  }
}
