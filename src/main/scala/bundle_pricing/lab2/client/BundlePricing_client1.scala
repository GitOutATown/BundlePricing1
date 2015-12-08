package bundle_pricing.lab2.client

import bundle_pricing.lab2.app._
import bundle_pricing.lab2.app.CartService._
import bundle_pricing.lab2.app.BundleService._
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global

object BundlePricing_client1 extends App {

    /** Simple items with unit price */
    val alpo = Item("Alpo Chicken 3oz", 0.89)
    val hotdogs = Item("Oscar Meyer Weiners 8 pack", 3.59)
    val charmin = Item("Charmin 4 roll", 3.00)
    val ipa = Item("Big Daddy IPA 6 pack", 7.99)
    val bread = Item("Wonder Bread", 3.00)
    val butter = Item("Butter Stick", 1.00)
    
    // Empty cart
    val cart1 = getCart
    
    // Items with quantity
    val shoppingList = List(
        (alpo, 1),
        (hotdogs, 1),
        (charmin, 2)
    )
    
    // Add shopping list to cart
    val cart2 = addToCart(shoppingList, cart1)
    
    
    val cart3 = addToCart(butter, 2, cart2)
    
    /** Bundles ***************/
    
    val breadAndButterBundle =
        forPriceOfQty(butter, 2, 1)((bread, 1))("Wonder Bread with 2 sticks butter, get second stick free.")
    val cart4 = addToCart(bread, 1, cart3)
    val result1 = checkout(cart3, List(breadAndButterBundle))
    
    checkout(cart4, List(breadAndButterBundle)).onComplete {
        case Success(cart) => printReceipt(cart)
        case Failure(e) => println(e)
    }
    
    Thread.sleep(2000)
}