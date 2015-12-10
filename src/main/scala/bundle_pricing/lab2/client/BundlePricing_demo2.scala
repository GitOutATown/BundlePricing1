package bundle_pricing.lab2.client

import bundle_pricing.lab2.app._
import bundle_pricing.lab2.app.CartService._
import bundle_pricing.lab2.app.BundleService._
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global

object BundlePricing_demo2 extends App {

    /*
     *  Simple items with unit price 
     */
    val alpo = Item("Alpo Chicken 3oz", 0.89)
    val hotdogs = Item("Oscar Meyer Weiners 8 pack", 3.59)
    val charmin = Item("Charmin 4 roll", 3.00)
    val ipa = Item("Big Daddy IPA 6 pack", 7.99)
    val bread = Item("Wonder Bread", 3.00)
    val butter = Item("Butter Stick", 1.00)
    
    /*
     *  Empty cart. 
     *  Carts (like all entities) are immutable.
     */
    val cart1 = getCart
    
    // Items with quantity
    val shoppingList = List(
        (alpo, 1),
        (hotdogs, 1),
        (charmin, 2)
    )
        
    // Bundles and checkout
    
    val cart2 = addToCart(alpo, 4, cart1)
    
    // Add shopping list to cart
    
    val cart3 = addToCart(shoppingList, cart2)
    
    val alpoBundle =
        forPriceOfQty(alpo, 5, 4)()("Alpo Chicken 3oz, 5 for price of 4")
  
    checkout(cart3, List(alpoBundle)).onComplete {
        case Success(cart) =>
            receipt(cart).onComplete{
                case Success(r) => println(r)
                case Failure(e) => println(s"Failed to print recipt: $e")
            }
        case Failure(e) => println(s"Checkout failed: $e") 
    }
    
    Thread.sleep(3000)
}