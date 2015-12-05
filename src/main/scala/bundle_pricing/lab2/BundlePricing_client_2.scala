package bundle_pricing.lab2

import bundle_pricing._

object BundlePricing_client_2 extends App {

    val i1 = Item("i1", 1.0, 0)
    val i2 = Item("i2", 2.0, 0)
    val i3 = Item("i3", 3.0, 10)
    
    val il1 = List(i1, i2, i3)
    println(il1)
}