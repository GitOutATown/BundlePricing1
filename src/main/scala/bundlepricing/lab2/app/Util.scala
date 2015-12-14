package bundlepricing.lab2.app

object Util {
    import scala.concurrent.Future
    import scala.concurrent.ExecutionContext.Implicits.global
    
    // Side-effect. TODO: Move this to separate file.    
    def receipt(cart: Cart): Future[String] = Future {
        val receiptBuilder = new StringBuilder("-------RECEIPT-------")
        cart.items.foreach { pricedItem => pricedItem match {
            case item: Item =>
                val identity = item.identity
                val price = round(item.price)
                receiptBuilder.append(s"\nITEM:\t$identity\t$price")
                
            case bundle: Bundle =>
                receiptBuilder.append("\n.....................")
                receiptBuilder.append(s"\nBUNDLE:")
                
                for{
                    bundleItem <- bundle.appliedTo
                    identity = bundleItem.item.identity
                    itemPrice = round(bundleItem.item.price)
                    qty = bundleItem.qty
                } yield receiptBuilder.append(s"\n$identity\tQTY:\t$qty")
                
                for{
                    addQualifier <- bundle.addQualifier
                } yield {
                    if(addQualifier != null) {
                        val aqIdent = addQualifier.item.identity
                        val aqQty = addQualifier.qty
                        receiptBuilder.append(s"\n$aqIdent\tQTY:\t$aqQty")
                    }
                }
                
                val regPrice = round(bundle.beforeDiscount)
                val bundPrice = round(bundle.price)
                val savings = round(regPrice - bundPrice)
                val description = bundle.description
                receiptBuilder.append(s"\n$description\t$bundPrice\nSAVINGS:\t$savings")
                receiptBuilder.append("\n.....................")
        }}
        val total = cart.total
        receiptBuilder.append(s"\nTOTAL\t\t$total")
        receiptBuilder.append("\n--------------------")
        
        receiptBuilder.toString
    } // end printReciept
    
    def roundAt(prec: Int)(n: Double): Double = {
        val scale = math pow(10, prec)
        (math round n * scale) / scale
    }
    val round = roundAt(2)_
}
