package bundle_pricing.lab2

import bundle_pricing.lab2.app._
import bundle_pricing.lab2.app.CartService._
import bundle_pricing.lab2.app.BundleService._

object BundlePricing_client_5 {

  /** Simple items with unit price */
  val alpo = Item("Alpo Chicken 3oz", 0.89)       //> alpo  : bundle_pricing.lab2.app.Item = Item(Alpo Chicken 3oz,0.89)
  val hotdogs = Item("Oscar Meyer Weiners 8 pack", 3.59)
                                                  //> hotdogs  : bundle_pricing.lab2.app.Item = Item(Oscar Meyer Weiners 8 pack,3.
                                                  //| 59)
  val charmin = Item("Charmin 4 roll", 3.00)      //> charmin  : bundle_pricing.lab2.app.Item = Item(Charmin 4 roll,3.0)
  val ipa = Item("Big Daddy IPA 6 pack", 7.99)    //> ipa  : bundle_pricing.lab2.app.Item = Item(Big Daddy IPA 6 pack,7.99)
  val bread = Item("Wonder Bread", 3.00)          //> bread  : bundle_pricing.lab2.app.Item = Item(Wonder Bread,3.0)
  val butter = Item("Butter Stick", 1.00)         //> butter  : bundle_pricing.lab2.app.Item = Item(Butter Stick,1.0)
  
  // Empty cart
  val cart1 = getCart                             //> cart1  : bundle_pricing.lab2.app.Cart = Cart(List(),0.0)
  
  // Items with quantity
  val shoppingList = List(
    (alpo, 1),
    (hotdogs, 1),
    (charmin, 2)
  )                                               //> shoppingList  : List[(bundle_pricing.lab2.app.Item, Int)] = List((Item(Alpo 
                                                  //| Chicken 3oz,0.89),1), (Item(Oscar Meyer Weiners 8 pack,3.59),1), (Item(Charm
                                                  //| in 4 roll,3.0),2))
  
  // Add shopping list to cart
  val cart2 = addToCart(shoppingList, cart1)      //> cart2  : bundle_pricing.lab2.app.Cart = Cart(List(Item(Charmin 4 roll,3.0), 
                                                  //| Item(Charmin 4 roll,3.0), Item(Oscar Meyer Weiners 8 pack,3.59), Item(Alpo C
                                                  //| hicken 3oz,0.89)),0.0)
  
  
  val cart3 = addToCart(butter, 2, cart2)         //> cart3  : bundle_pricing.lab2.app.Cart = Cart(List(Item(Butter Stick,1.0), It
                                                  //| em(Butter Stick,1.0), Item(Charmin 4 roll,3.0), Item(Charmin 4 roll,3.0), It
                                                  //| em(Oscar Meyer Weiners 8 pack,3.59), Item(Alpo Chicken 3oz,0.89)),0.0)
  
  /** Bundles ***************/
  
  val breadAndButterBundle =
    forPriceOfQty(butter, 2, 1)((bread, 1))("Buy Wonder Bread with 2 sticks of butter, get second stick free.")
                                                  //> breadAndButterBundle  : bundle_pricing.lab2.app.Bundle = Bundle(ForPriceOf(1
                                                  //| ),List(BundleItem(Item(Butter Stick,1.0),2)),List(BundleItem(Item(Wonder Bre
                                                  //| ad,3.0),1)),Buy Wonder Bread with 2 sticks of butter, get second stick free.
                                                  //| )
  val cart4 = addToCart(bread, 1, cart3)          //> cart4  : bundle_pricing.lab2.app.Cart = Cart(List(Item(Wonder Bread,3.0), It
                                                  //| em(Butter Stick,1.0), Item(Butter Stick,1.0), Item(Charmin 4 roll,3.0), Item
                                                  //| (Charmin 4 roll,3.0), Item(Oscar Meyer Weiners 8 pack,3.59), Item(Alpo Chick
                                                  //| en 3oz,0.89)),0.0)
  val result1 = checkout(cart3, List(breadAndButterBundle))
                                                  //> result1  : bundle_pricing.lab2.app.Cart = Cart(List(Item(Butter Stick,1.0),
                                                  //|  Item(Butter Stick,1.0), Item(Charmin 4 roll,3.0), Item(Charmin 4 roll,3.0)
                                                  //| , Item(Oscar Meyer Weiners 8 pack,3.59), Item(Alpo Chicken 3oz,0.89)),12.48
                                                  //| )
  
  val result2 = checkout(cart4, List(breadAndButterBundle))
                                                  //> result2  : bundle_pricing.lab2.app.Cart = Cart(List(AppliedBundle(List(Bund
                                                  //| leItem(Item(Butter Stick,1.0),2)),1.0,Buy Wonder Bread with 2 sticks of but
                                                  //| ter, get second stick free.), Item(Charmin 4 roll,3.0), Item(Charmin 4 roll
                                                  //| ,3.0), Item(Oscar Meyer Weiners 8 pack,3.59), Item(Alpo Chicken 3oz,0.89)),
                                                  //| 11.48)
  printReceipt(result2)                           //> BUNDLE:		Butter Stick		QTY:		2
                                                  //| ====>regPrice * qty: 2.0
                                                  //| SPECIAL:		Buy Wonder Bread with 2 sticks of butter, get second sti
                                                  //| ck free.		1.0		SAVINGS:		1.0
                                                  //| ITEM:		Charmin 4 roll		3.0
                                                  //| ITEM:		Charmin 4 roll		3.0
                                                  //| ITEM:		Oscar Meyer Weiners 8 pack		3.59
                                                  //| ITEM:		Alpo Chicken 3oz		0.89
                                                  //| TOTAL		11.48
  
  '''                                             //> res0: Char('\'') = '
}