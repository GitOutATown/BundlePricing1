package bundle_pricing.lab2

import org.scalatest._
import org.scalatest.concurrent.ScalaFutures

abstract class UnitSpec extends FlatSpec with Matchers 
    with PrivateMethodTester with ScalaFutures {}