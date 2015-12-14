package bundlepricing.app

import scala.util.{Try, Success, Failure}

trait Priced { def price: Double }

case class Item(
    identity: String,
    price: Double
) extends Priced { require(price > 0) }

object CatalogService {}