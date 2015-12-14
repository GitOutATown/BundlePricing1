package bundlepricing.app

import scala.util.{Try, Success, Failure}

trait Priced { def price: Double }

case class Item(
    identity: String,
    price: Double
) extends Priced {
    // instantiation constraints
    require(identity != "")
    require(price > 0) 
}

// TODO: Factory with Try/Success/Failure