package bundle_pricing.lab2.client

import bundle_pricing.lab2.app._
import bundle_pricing.lab2.app.CartService._
import bundle_pricing.lab2.app.BundleService._
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global

object MultipleBundles_demo6 extends App {
    
    /*
     * Combination of bundles. flat2 and flat3 are applied. flat1 is beat out.
     */
    
    val i1 = Item("One", 2.0)
    val i2 = Item("Two", 2.0)
    val i3 = Item("Three", 2.0)
    val addQual = Item("Something Extra", 1.0)

    val shoppingList = List((i1, 3), (i2, 3), (i3, 3), (addQual, 1))
    
    val cart = getCart
    
    val cart2 = addToCart(shoppingList, cart)
    
    val flat1 = bundlePrice(2.00, (i1, 2))()("2 i1 for $2.00")
    val flat2 = bundlePrice(1.50, (i2, 2))()("2 i2 for $1.50")
    val flat3 = bundlePrice(3.00, (i1, 2),(i3, 2))()("2 i1 and 2 i3 for $3.00")
    
    val bundles = List(flat1, flat2, flat3)
    
    checkout(cart2, bundles).onComplete {
        case Success(cart) => printReceipt(cart)
        case Failure(e) => println(e) 
    }
    
    Thread.sleep(3000)
}