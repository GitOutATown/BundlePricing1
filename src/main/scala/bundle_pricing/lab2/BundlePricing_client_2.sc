package bundle_pricing.lab2

import bundle_pricing.lab2.app._
import bundle_pricing.lab2.app.CartService._
import bundle_pricing.lab2.app.BundleService._

object BundlePricing_client_2 {

  /** Simple items with unit price */
  val alpo = Item("Alpo Chicken 3oz", 0.89)       //> alpo  : bundle_pricing.lab2.app.Item = Item(Alpo Chicken 3oz,0.89)
  val hotdogs = Item("Oscar Meyer Weiners 8 pack", 3.59)
                                                  //> hotdogs  : bundle_pricing.lab2.app.Item = Item(Oscar Meyer Weiners 8 pack,3.
                                                  //| 59)
  val charmin = Item("Charmin 4 roll", 3.00)      //> charmin  : bundle_pricing.lab2.app.Item = Item(Charmin 4 roll,3.0)
  val ipa = Item("Big Daddy IPA 6 pack", 7.99)    //> ipa  : bundle_pricing.lab2.app.Item = Item(Big Daddy IPA 6 pack,7.99)
  
  // Empty cart
  val cart1 = getCart                             //> cart1  : bundle_pricing.lab2.app.Cart = Cart(List())
  
  // Add single item with quantity to cart
  val cart2 = addToCart(alpo, 4, cart1)           //> cart2  : bundle_pricing.lab2.app.Cart = Cart(List(Item(Alpo Chicken 3oz,0.89
                                                  //| ), Item(Alpo Chicken 3oz,0.89), Item(Alpo Chicken 3oz,0.89), Item(Alpo Chick
                                                  //| en 3oz,0.89)))
  
  // Items with quantity
  val shoppingList = List(
    (alpo, 1),
    (hotdogs, 1),
    (charmin, 2)
  )                                               //> shoppingList  : List[(bundle_pricing.lab2.app.Item, Int)] = List((Item(Alpo 
                                                  //| Chicken 3oz,0.89),1), (Item(Oscar Meyer Weiners 8 pack,3.59),1), (Item(Charm
                                                  //| in 4 roll,3.0),2))
  
  val cart3 = addToCart(shoppingList, cart2)      //> cart3  : bundle_pricing.lab2.app.Cart = Cart(List(Item(Charmin 4 roll,3.0), 
                                                  //| Item(Charmin 4 roll,3.0), Item(Oscar Meyer Weiners 8 pack,3.59), Item(Alpo C
                                                  //| hicken 3oz,0.89), Item(Alpo Chicken 3oz,0.89), Item(Alpo Chicken 3oz,0.89), 
                                                  //| Item(Alpo Chicken 3oz,0.89), Item(Alpo Chicken 3oz,0.89)))
  
  /** Bundles ***************/
  
  val alpoBundle =
    forPriceOfQty(alpo, 5, 4)()("Alpo Chicken 3oz, 5 for price of 4")
                                                  //> alpoBundle  : bundle_pricing.lab2.app.Bundle = Bundle(ForPriceOf(4),List(Bun
                                                  //| dleItem(Item(Alpo Chicken 3oz,0.89),5)),List(),Alpo Chicken 3oz, 5 for price
                                                  //|  of 4)
  
  bundleMatch(cart2, alpoBundle)                  //> -->bundleMatch cartItems: List(Item(Alpo Chicken 3oz,0.89), Item(Alpo Chicke
                                                  //| n 3oz,0.89), Item(Alpo Chicken 3oz,0.89), Item(Alpo Chicken 3oz,0.89))
                                                  //| -->bundleMatch bundle.appliedTo: List(BundleItem(Item(Alpo Chicken 3oz,0.89)
                                                  //| ,5))
                                                  //| -->bundleMatch bundle.appliedTo: List()
                                                  //| res0: Boolean = false
  
  
  
  bundleMatch(cart3, alpoBundle)                  //> -->bundleMatch cartItems: List(Item(Charmin 4 roll,3.0), Item(Charmin 4 roll
                                                  //| ,3.0), Item(Oscar Meyer Weiners 8 pack,3.59), Item(Alpo Chicken 3oz,0.89), I
                                                  //| tem(Alpo Chicken 3oz,0.89), Item(Alpo Chicken 3oz,0.89), Item(Alpo Chicken 3
                                                  //| oz,0.89), Item(Alpo Chicken 3oz,0.89))
                                                  //| -->bundleMatch bundle.appliedTo: List(BundleItem(Item(Alpo Chicken 3oz,0.89)
                                                  //| ,5))
                                                  //| -->bundleMatch bundle.appliedTo: List()
                                                  //| res1: Boolean = true
        
  '''                                             //> res2: Char('\'') = '
}