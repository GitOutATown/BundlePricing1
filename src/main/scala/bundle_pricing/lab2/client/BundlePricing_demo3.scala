package bundle_pricing.lab2.client

import bundle_pricing.lab2.app._
import bundle_pricing.lab2.app.CartService._
import bundle_pricing.lab2.app.BundleService._
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global

object BundlePricing_demo3 extends App {
    
    /**
     * In this demo the cart meets the flat price bundle requirements
     * and set one flat price for all of the applied items. No other
     * additional qualifiers are included.
     */

    val i1 = Item("One", 1.0)
    val i2 = Item("Two", 1.0)
    val i3 = Item("Three", 1.0)
    
    val shoppingList = List((i1, 3), (i2, 1), (i3, 2))
    
    val cart = getCart
    
    val cart2 = addToCart(shoppingList, cart)
    
    val flatPrice = bundlePrice(3.0, (i1, 3), (i2, 1), (i3, 2))()("Test flat price.")

    checkout(cart2, List(flatPrice)).onComplete {
        case Success(cart) =>
            receipt(cart).onComplete{
                case Success(r) => println(r)
                case Failure(e) => println(s"Failed to print recipt: $e")
            }
        case Failure(e) => println(s"Checkout failed: $e")  
    }
    
    Thread.sleep(3000)
}


