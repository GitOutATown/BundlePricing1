package bundle_pricing.lab2

import bundle_pricing.lab2.app._
import bundle_pricing.lab2.app.CartService._
import bundle_pricing.lab2.app.BundleService._

object BundlePricing_client_4 {

  /** Simple items with unit price */
  val alpo = Item("Alpo Chicken 3oz", 0.89)       //> alpo  : bundle_pricing.lab2.app.Item = Item(Alpo Chicken 3oz,0.89)
  val hotdogs = Item("Oscar Meyer Weiners 8 pack", 3.59)
                                                  //> hotdogs  : bundle_pricing.lab2.app.Item = Item(Oscar Meyer Weiners 8 pack,3.
                                                  //| 59)
  val charmin = Item("Charmin 4 roll", 3.00)      //> charmin  : bundle_pricing.lab2.app.Item = Item(Charmin 4 roll,3.0)
  val ipa = Item("Big Daddy IPA 6 pack", 7.99)    //> ipa  : bundle_pricing.lab2.app.Item = Item(Big Daddy IPA 6 pack,7.99)
  val bread = Item("Wonder Bread", 1.29)          //> bread  : bundle_pricing.lab2.app.Item = Item(Wonder Bread,1.29)
  val butter = Item("Butter Stick", 0.89)         //> butter  : bundle_pricing.lab2.app.Item = Item(Butter Stick,0.89)
  
  val shoppingList = List(
    alpo, hotdogs
  )                                               //> shoppingList  : List[bundle_pricing.lab2.app.Item] = List(Item(Alpo Chicken 
                                                  //| 3oz,0.89), Item(Oscar Meyer Weiners 8 pack,3.59))
  
  0.89 + 3.59                                     //> res0: Double(4.4799999999999995) = 4.4799999999999995
  
  itemsTotal(shoppingList)                        //> res1: Double = 4.48

  '''                                             //> res2: Char('\'') = '
}