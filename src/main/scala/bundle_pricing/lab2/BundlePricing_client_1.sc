package bundle_pricing.lab2

import bundle_pricing.lab2.app._
import bundle_pricing.lab2.app.CartService._

object BundlePricing_client_1 {

  val i1 = Item("i1", 1.0, 0)                     //> i1  : bundle_pricing.lab2.app.Item = Item(i1,1.0,0)
  val i2 = Item("i2", 2.0, 0)                     //> i2  : bundle_pricing.lab2.app.Item = Item(i2,2.0,0)
  val i3 = Item("i3", 3.0, 10)                    //> i3  : bundle_pricing.lab2.app.Item = Item(i3,3.0,10)
    
  val items1 = List(i1, i2, i3)                   //> items1  : List[bundle_pricing.lab2.app.Item] = List(Item(i1,1.0,0), Item(i2,
                                                  //| 2.0,0), Item(i3,3.0,10))
  val cart1 = getCart                             //> cart1  : bundle_pricing.lab2.app.Cart = Cart(List())
  
  addItems(items1, cart1)                         //> res0: bundle_pricing.lab2.app.Cart = Cart(List(Item(i1,1.0,0), Item(i2,2.0,0
                                                  //| ), Item(i3,3.0,10)))
  
  '''                                             //> res1: Char('\'') = '
  
}