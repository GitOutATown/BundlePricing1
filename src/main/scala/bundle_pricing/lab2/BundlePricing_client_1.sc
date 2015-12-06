package bundle_pricing.lab2

import bundle_pricing.lab2.app._
import bundle_pricing.lab2.app.CartService._

object BundlePricing_client_1 {

  /** Simple items with unit price */
  val alpo = StoreItem("Alpo Chicken 3oz", 0.89)  //> alpo  : bundle_pricing.lab2.app.StoreItem = StoreItem(Alpo Chicken 3oz,0.89)
                                                  //| 
  val hotdogs = StoreItem("Oscar Meyer Weiners 8 pack", 3.59)
                                                  //> hotdogs  : bundle_pricing.lab2.app.StoreItem = StoreItem(Oscar Meyer Weiners
                                                  //|  8 pack,3.59)
  val charmin = StoreItem("Charmin 4 roll", 3.00) //> charmin  : bundle_pricing.lab2.app.StoreItem = StoreItem(Charmin 4 roll,3.0)
                                                  //| 
  val ipa = StoreItem("Big Daddy IPA 6 pack", 7.99)
                                                  //> ipa  : bundle_pricing.lab2.app.StoreItem = StoreItem(Big Daddy IPA 6 pack,7.
                                                  //| 99)
  
  // Items with quantity
  val shoppingList = List(
    (alpo, 4),
    (hotdogs, 1),
    (charmin, 2)
  )                                               //> shoppingList  : List[(bundle_pricing.lab2.app.StoreItem, Int)] = List((Store
                                                  //| Item(Alpo Chicken 3oz,0.89),4), (StoreItem(Oscar Meyer Weiners 8 pack,3.59),
                                                  //| 1), (StoreItem(Charmin 4 roll,3.0),2))
  // Empty cart
  val cart1 = getCart                             //> cart1  : bundle_pricing.lab2.app.Cart = Cart(List())
  
  // Add single item with quantity to cart
  val cart2 = addToCart(alpo, 4, cart1)           //> cart2  : bundle_pricing.lab2.app.Cart = Cart(List(CartItem(StoreItem(Alpo Ch
                                                  //| icken 3oz,0.89),4)))
  // Add multiple items with quantities to cart
  val cart3 = addToCart(shoppingList, cart2)      //> cart3  : bundle_pricing.lab2.app.Cart = Cart(List(CartItem(StoreItem(Charmin
                                                  //|  4 roll,3.0),2), CartItem(StoreItem(Oscar Meyer Weiners 8 pack,3.59),1), Car
                                                  //| tItem(StoreItem(Alpo Chicken 3oz,0.89),4), CartItem(StoreItem(Alpo Chicken 3
                                                  //| oz,0.89),4)))
   
  '''                                             //> res0: Char('\'') = '
}