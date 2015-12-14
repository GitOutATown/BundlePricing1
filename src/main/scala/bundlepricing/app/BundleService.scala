package bundlepricing.app

private[app] sealed trait Discount
private[app] case class PercentOff(pct: Double) extends Discount {
    require(pct > 0)
}
private[app] case class BundlePrice(flat: Double) extends Discount {
    require(flat > 0)
}
private[app] case class ForPriceOf(qty: Int) extends Discount {
    require(qty > 0)
}

/** 
 *  Aggregation of required items, with applied discount.
 *  Bundle price may be result of either percentage or flat or N for M discounts, 
 *  but is applied as an aggregate to all BundleItems. Current model, however, 
 *  can only hold multiple appliedTo BundleItems for flat price discount.
 *  
 *  @param discount is the Discount type (i.e. percent off, flat rate, N for M)
 *  
 *  @param appliedTo are the items whose prices are affected by the discount. Currently
 *  only BundlePrice discount can support multiple appliedTo BundleItems.
 *  
 *  @param addQualifier is not directly affected by the discount. If extant, it 
 *  is a bundle requirement. Its regular price is included in the aggregated 
 *  bundle price.
 *  
 *  @param description is the description of the bundle discount (i.e. the type,
 *  the items which directly receive the discount, and the other required items.
 *  
 *  @param beforeDiscount is aggregated bundle price before discount applied to 
 *  appliedTo items.
 *  
 *  @param price is the aggregated price for all items in the bundle
 *  including appliedTo and addQualifier. bundlePrice, at this iterative
 *  stage, is primarily a mechanism for tallying the cart total. It also 
 *  indicates the value of the bundle and is used to calculate savings.
 */
private[app] case class Bundle(
    discount: Discount,
    appliedTo: List[BundleItem],
    addQualifier: List[BundleItem],
    description: String,
    beforeDiscount: Double = 0.0,
    price: Double = 0.0
) extends Priced

private[app] case class BundleItem(
    item: Item,
    qty: Int
) { require(qty > 0)}

object BundleService {
    
    ///// API Bundle factories /////
    // TODO: Factor out commonalities
    // TODO: Replace Discount case class with function parameter
        
    /**
     *  N qty of an item for price of M qty of same item.
     *  May have additional qualifiers.
     */
    def forPriceOfQty(discountItem: Item, nQty: Int, mQty: Int)
                     (addQualifier: (Item, Int)*)
                     (description: String): Bundle = {
        // instantiation constraints
        require(nQty > 0) 
        require(mQty > 0)
        
        val discount = ForPriceOf(mQty) // TODO: Make this a function?
        val appliedTo = BundleItem(discountItem, nQty)
        val addQualifier_ = 
            addQualifier.toList.map{ case (item, qty) => BundleItem(item, qty) }
        Bundle(discount, List(appliedTo), addQualifier_, description)
    }
    
    /** 
     *  N quantity of an item earns percent off the list price.
     *  Handles simple case of percent off list price for one or any
     *  number of same item.
     *  May have additional qualifiers.
     *  
     *  TODO: Multiple items with various quantities, each for percent off of 
     *  their list price.
     */ 
    def percentPrice(discountItem: Item, qty: Int, pctOff: Double)
                    (addQualifier: (Item, Int)*)
                    (description: String): Bundle = {
        val discount = PercentOff(pctOff)
        val appliedTo = BundleItem(discountItem, qty)
        val addQualifier_ = addQualifier.toList.map{
            case (item, qty) => BundleItem(item, qty)}
        Bundle(discount, List(appliedTo), addQualifier_, description)
    }
    
    /**
     *  Multiple items of various specific quantities for flat price.
     *  Handles simpler case of N quantity of one item for flat price.
     *  May have additional qualifiers.
     */
    def bundlePrice(flatPrice: Double, discountItem: (Item, Int)*)
                   (addQualifier: (Item, Int)*)
                   (description: String): Bundle = {
        val discount = BundlePrice(flatPrice)
        val appliedTo = discountItem.toList.map{
            case (item, qty) => BundleItem(item, qty)}
        val addQualifier_ = addQualifier.toList.map{
            case (item, qty) => BundleItem(item, qty)}
        Bundle(discount, appliedTo, addQualifier_, description)
    }
}
