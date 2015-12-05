package bundle_pricing.lab2

object BundlePricing_client_1 {

  val i1 = Item("i1", 1.0, 0)                     //> i1  : bundle_pricing.lab2.Item = Item(i1,1.0,0)
  val i2 = Item("i2", 2.0, 0)                     //> i2  : bundle_pricing.lab2.Item = Item(i2,2.0,0)
  val i3 = Item("i3", 3.0, 10)                    //> i3  : bundle_pricing.lab2.Item = Item(i3,3.0,10)
    
  val il1 = List(i1, i2, i3)                      //> il1  : List[bundle_pricing.lab2.Item] = List(Item(i1,1.0,0), Item(i2,2.0,0),
                                                  //|  Item(i3,3.0,10))

  '''                                             //> res0: Char('\'') = '
}