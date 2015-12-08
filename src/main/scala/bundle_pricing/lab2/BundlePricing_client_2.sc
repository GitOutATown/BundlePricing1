package bundle_pricing.lab2

import bundle_pricing.lab2.app._
import bundle_pricing.lab2.app.CartService._
import bundle_pricing.lab2.app.BundleService._

import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global

object BundlePricing_client_2 {

  /** Simple items with unit price */
  val alpo = Item("Alpo Chicken 3oz", 0.89)
  val hotdogs = Item("Oscar Meyer Weiners 8 pack", 3.59)
  val charmin = Item("Charmin 4 roll", 3.00)
  val ipa = Item("Big Daddy IPA 6 pack", 7.99)
  
  // Empty cart
  val cart1 = getCart
  
  // Add single item with quantity to cart
  val cart2 = addToCart(alpo, 4, cart1)
  
  // Items with quantity
  val shoppingList = List(
    (alpo, 1),
    (hotdogs, 1),
    (charmin, 2)
  )
  
  val cart3 = addToCart(shoppingList, cart2)
  
  /** Bundles ***************/
  
  val alpoBundle =
    forPriceOfQty(alpo, 5, 4)()("Alpo Chicken 3oz, 5 for price of 4")
  checkout(cart3, List(alpoBundle)).onComplete {
    case Success(cart) => printReceipt(cart)
    case Failure(e) => println(e)
  }
  
  Thread.sleep(2000)
                                   
  

       
  '''~
}