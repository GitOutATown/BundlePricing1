package bundle_pricing.lab2

import bundle_pricing.lab2.app._
import bundle_pricing.lab2.app.CartService._
import bundle_pricing.lab2.app.BundleService._

object BundlePricing_client_2 {

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
  
  // Empty cart
  val cart1 = getCart                             //> cart1  : bundle_pricing.lab2.app.Cart = Cart(List())
  
  // Add single item with quantity to cart
  val cart2 = addToCart(alpo, 4, cart1)           //> cart2  : bundle_pricing.lab2.app.Cart = Cart(List(CartItem(StoreItem(Alpo Ch
                                                  //| icken 3oz,0.89),4)))
  
  /** Bundles ***************/
  
  val alpoBundle = qtyForPrice(alpo, 10, 3.00)    //> alpoBundle  : bundle_pricing.lab2.app.Bundle = Bundle(List(BundleItem(StoreI
                                                  //| tem(Alpo Chicken 3oz,0.89),10)),AppliedHow(BundlePrice(3.0),BundleItem(Store
                                                  //| Item(Alpo Chicken 3oz,0.89),10)))
  
  bundleMatch(cart2, alpoBundle)                  //> -->bundleMatch cartItems: List(CartItem(StoreItem(Alpo Chicken 3oz,0.89),4))
                                                  //| 
                                                  //| -->bundleMatch bundle.qualifier: List(BundleItem(StoreItem(Alpo Chicken 3oz,
                                                  //| 0.89),10))
                                                  //| -->bundleMatch head: BundleItem(StoreItem(Alpo Chicken 3oz,0.89),10)
                                                  //| -->bundleMatch qualifierCount: 0
                                                  //| -->bundleMatch result<1>: true
                                                  //| res0: Boolean = true
  
  /*
  StoreItem("Alpo Chicken 3oz", 0.89) == StoreItem("Alpo Chicken 3oz", 0.99)
  alpo == alpo
  StoreItem("Alpo Chicken 3oz", 0.89) == alpo
  StoreItem("Alpo Chicken 3oz", 0.99) == alpo
  */
  '''                                             //> res1: Char('\'') = '
}