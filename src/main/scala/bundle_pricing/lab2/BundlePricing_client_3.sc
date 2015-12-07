package bundle_pricing.lab2

import bundle_pricing.lab2.app._
import bundle_pricing.lab2.app.CartService._
import bundle_pricing.lab2.app.BundleService._

object BundlePricing_client_3 {

  /** Simple items with unit price */
  val alpo = Item("Alpo Chicken 3oz", 0.89)
  val hotdogs = Item("Oscar Meyer Weiners 8 pack", 3.59)
  val charmin = Item("Charmin 4 roll", 3.00)
  val ipa = Item("Big Daddy IPA 6 pack", 7.99)
  val bread = Item("Wonder Bread", 1.29)
  val butter = Item("Butter Stick", 0.89)
  
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
    forPriceOfQty(butter, 2, 4)((bread, 1))("Buy loaf of Wonder Bread with 2 sticks of butter and get the second stick free.")
  bundleMatch(cart3, breadAndButterBundle)
  val cart4 = addToCart(bread, 1, cart3)
  
  bundleMatch(cart4, breadAndButterBundle)
  
~
  '''
}