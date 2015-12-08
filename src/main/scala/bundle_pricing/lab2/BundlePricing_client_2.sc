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
  val cart1 = getCart                             //> cart1  : bundle_pricing.lab2.app.Cart = Cart(List(),0.0)
  
  // Add single item with quantity to cart
  val cart2 = addToCart(alpo, 4, cart1)           //> cart2  : bundle_pricing.lab2.app.Cart = Cart(List(Item(Alpo Chicken 3oz,0.89
                                                  //| ), Item(Alpo Chicken 3oz,0.89), Item(Alpo Chicken 3oz,0.89), Item(Alpo Chick
                                                  //| en 3oz,0.89)),0.0)
  
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
                                                  //| Item(Alpo Chicken 3oz,0.89), Item(Alpo Chicken 3oz,0.89)),0.0)
  
  /** Bundles ***************/
  
  val alpoBundle =
    forPriceOfQty(alpo, 5, 4)()("Alpo Chicken 3oz, 5 for price of 4")
                                                  //> alpoBundle  : bundle_pricing.lab2.app.Bundle = Bundle(ForPriceOf(4),List(Bun
                                                  //| dleItem(Item(Alpo Chicken 3oz,0.89),5)),List(),Alpo Chicken 3oz, 5 for price
                                                  //|  of 4,0.0,0.0)
  val result1 = checkout(cart3, List(alpoBundle)) //> result1  : bundle_pricing.lab2.app.Cart = Cart(List(Bundle(ForPriceOf(4),Lis
                                                  //| t(BundleItem(Item(Alpo Chicken 3oz,0.89),5)),List(),Alpo Chicken 3oz, 5 for 
                                                  //| price of 4,4.45,3.56), Item(Charmin 4 roll,3.0), Item(Charmin 4 roll,3.0), I
                                                  //| tem(Oscar Meyer Weiners 8 pack,3.59)),13.15)
  println("-----------------")                    //> -----------------
  printReceipt(result1)                           //> Alpo Chicken 3oz, 5 for price of 4	3.56
                                                  //| SAVINGS:	0.89
                                                  //| ITEM:		Charmin 4 roll		3.0
                                                  //| ITEM:		Charmin 4 roll		3.0
                                                  //| ITEM:		Oscar Meyer Weiners 8 pack		3.59
                                                  //| TOTAL		13.15
                                    
  

        
  '''                                             //> res0: Char('\'') = '
}