package bundle_pricing.lab2.client

import bundle_pricing.lab2.app._
import bundle_pricing.lab2.app.CartService._
import bundle_pricing.lab2.app.BundleService._
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global

object MultipleBundles_demo7 extends App {
    
    /*
     * Combination of bundles. flat1 and flat2 are applied. flat3 is beat out.
     */
    
    val i1 = Item("One", 1.0)
    val i2 = Item("Two", 2.0)
    val i3 = Item("Three", 2.0)
    val addQual = Item("Something Extra", 1.0)

    val shoppingList = List((i1, 4), (i2, 3), (i3, 3), (addQual, 1))
    
    val cart = getCart
    
    val cart2 = addToCart(shoppingList, cart)
    
    val flat1 = bundlePrice(1.00, (i1, 4))()("4 i1 for $1.00")
    val flat2 = bundlePrice(1.75, (i2, 2))()("2 i2 for $1.75")
    val flat3 = bundlePrice(4.00, (i1, 2),(i3, 2))()("2 i1 and 2 i3 for $4.00")
    
    val bundles = List(flat1, flat2, flat3)
    
    checkout(cart2, bundles).onComplete {
        case Success(cart) => printReceipt(cart)
        case Failure(e) => println(e) 
    }
    
    Thread.sleep(3000)
}