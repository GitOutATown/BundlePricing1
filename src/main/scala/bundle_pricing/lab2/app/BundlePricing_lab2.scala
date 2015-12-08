package bundle_pricing.lab2.app

sealed trait PricedItem { def price: Double }

case class Item(
    identity: String,
    price: Double
) extends PricedItem

private[app] case class Cart(
    items: List[PricedItem],
    total: Double = 0.0
)

object CartService {
    import Util.round
    
    def getCart() = Cart(Nil)
    
    // Single item
    def addToCart(item: Item, cart: Cart): Cart = {
        cart.copy(items = item :: cart.items)
    }
    
    // Multiple of same item
    def addToCart(item: Item, qty: Int, cart: Cart): Cart = {
        if(qty == 0) cart
        else addToCart(item, qty - 1, cart.copy(items = item :: cart.items))
    }
    
    // Multiple items, each with variable quanties
    @annotation.tailrec
    def addToCart(items: List[(Item, Int)], cart: Cart): Cart = items match {
        case Nil => cart
        case (item, qty) :: tail => addToCart(tail, addToCart(item, qty, cart))
    }
    
    // TODO: Return future
    /** Calculates minimum cart total per best combination of bundle discounts. */
    def checkout(cart: Cart, bundles: List[Bundle]): Cart = {
        val applicableBundles = bundles.filter(bundleMatch(cart, _))
        val bundlePerms = (applicableBundles.permutations).toList
        // Each iteration has original cart but different bundle order. Every possible sequence of bundles are tried.
        val allCartVersions = bundlePerms.map(applyBundles(cart, _))
        val minCart = allCartVersions.reduceLeft(minTotal)
        minCart
    }
    
    /* TODO: Be careful: Cart.items are now PricedItem, which allows for 
     * AppliedBundleItems. This is a risk!
     */
    // Purpose: initial filter for cart relevancy based on adequate items
    // without consideration for overlap with other bundles.
    private[app] def bundleMatch(cart: Cart, bundle: Bundle): Boolean = {
        val required = bundle.appliedTo ++ bundle.addQualifier
        required.forall{ bundleItem =>
            cart.items.count(_ == bundleItem.item) >= bundleItem.qty }
    }
    
    /** Precicely matches and replaces loose cart items with BundleItems (item qty 
     *  with discount). If bundle can't be applied, leaves cart items as found.
     */
    private[app] def applyBundles(cart: Cart, bundles: List[Bundle]): Cart = bundles match {
        case Nil => cartTotal(cart) // All bundles have been applied or tried.
        case bundle :: tail => applyBundles(applyBundle(cart, bundle), tail)
    }
    
    /** Process all BundleItems for this Bundle. */
    private[app] def applyBundle(cart: Cart, bundle: Bundle): Cart = {
        // Recursive
        def inner(
            items: List[PricedItem], bundleItems: List[BundleItem]
        ): List[PricedItem] = bundleItems match {
            case Nil => // Successful application of all BundleItems. Any failure shorcuts to outer, returning original cart.items.
                val appliedBundle = applyDiscount(bundle) 
                appliedBundle :: items 
            case bundleItem :: tail => {
                val targetItem = bundleItem.item
                val count = 0
                val targetQty = bundleItem.qty
                val acc = List[PricedItem]()
                
                // TODO: Replace tuple with BundleContext case class
                val context = (acc, count, targetQty, targetItem)
                
                // Remove loose cart items that exist in BundleItem.
                val result = items.foldRight(context)((item, context) => {
                    // if count < targetQty filter out targetItem
                    if(item == targetItem && context._2 < context._3)
                        (context._1, context._2 + 1, context._3, context._4)
                    else (item :: context._1, context._2, context._3, context._4)
                })
                
                // if count == targetQty we have successful application of BundleItem, so recurse with bundle items removed from loose cart items.
                if(result._2 == result._3) inner(result._1, tail)
                // else unsuccessful application of BundleItem so return to outer with original cart items.
                else cart.items
            }
        } // end inner
        
        // Start recursion of BundleItems for this Bundle.
        val appliedItems = inner(cart.items, bundle.appliedTo ++ bundle.addQualifier)
        
        // Return outer. Could be either successful or unsuccessful BundleItem application.
        Cart(appliedItems)
    }
    
    // TODO: Replace with parameterized function instead of hard coded types which have to be referenced and maintained in two separate places (i.e. case class and this function).
    private[app] def applyDiscount(bundle: Bundle): AppliedBundle = 
        bundle.discount match {
        
        case pctOff: PercentOff =>
            val originalPrice = (for(
                bundleItem <- bundle.appliedTo
            ) yield bundleItem.item.price * bundleItem.qty).sum
            val percentOff = originalPrice * pctOff.pct
            AppliedBundle(
                bundle.appliedTo, 
                originalPrice - percentOff, 
                bundle.description
            )
            
        case flatPrice: BundlePrice => 
            AppliedBundle(bundle.appliedTo, flatPrice.flat, bundle.description)
        
        case fewerQty: ForPriceOf =>
            val fewerPrice = (for(
                bundleItem <- bundle.appliedTo  
            ) yield bundleItem.item.price * fewerQty.qty).sum
            AppliedBundle(bundle.appliedTo, fewerPrice, bundle.description)
    }
    
    private[app] def cartTotal(cart: Cart): Cart = {
        val result = cart.items.foldRight(0.0)((item, acc) => item.price + acc)
        cart.copy(total = round(result))
    }
    
    private[app] def minTotal(a: Cart, b: Cart): Cart = if(a.total < b.total) a else b

    // Side-effect
    def printReceipt(cart: Cart) {
        cart.items.foreach { pricedItem => pricedItem match {
            case item: Item =>
                val identity = item.identity
                val price = item.price
                println(s"ITEM:\t\t$identity\t\t$price")
            case bundle: AppliedBundle =>
                val regPrice = (for{
                    bundleItem <- bundle.appliedTo
                    identity = bundleItem.item.identity
                    regPrice = bundleItem.item.price
                    qty = bundleItem.qty
                } yield {
                    println(s"BUNDLE:\t\t$identity\t\tQTY:\t\t$qty")
                    regPrice * qty
                }).sum
                val bundPrice = round(bundle.price)
                val savings = regPrice - bundPrice
                val description = bundle.description
                println(s"SPECIAL:\t\t$description\t\t$bundPrice\t\tSAVINGS:\t\t$savings")
        }}
        val total = cart.total
        println(s"TOTAL\t\t$total")
    }
}

private[app] sealed trait Discount
private[app] case class PercentOff(pct: Double) extends Discount
private[app] case class BundlePrice(flat: Double) extends Discount
private[app] case class ForPriceOf(qty: Int) extends Discount

/** Aggregation of required items, with applied discount */
private[app] case class Bundle(
    discount: Discount,
    appliedTo: List[BundleItem],
    addQualifier: List[BundleItem],
    description: String
)

private[app] case class BundleItem(
    item: Item,
    qty: Int
)

/** Bundle price may be result of either percentage or flat or N for M discounts, 
 *  but is applied to all BundleItems. Current model, however, can only hold 
 *  multiple BundleItems for flat price discount.
 */
// TODO: Nix this and add price field and extend PricedItem
private[app] case class AppliedBundle(
    appliedTo: List[BundleItem],
    price: Double,
    description: String
) extends PricedItem

object BundleService {
    ///// Bundle factories /////
    // TODO: Factor out commonalities
    // TODO: Replace Discount case class with function parameter
    
    /**
     *  N qty of an item for price of M qty of same item.
     *  May have additional qualifiers.
     */
    def forPriceOfQty(discountItem: Item, nQty: Int, mQty: Int)
                     (addQualifier: (Item, Int)*)
                     (description: String): Bundle = {
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

object Util {
    def roundAt(prec: Int)(n: Double): Double = {
        val scale = math pow(10, prec)
        (math round n * scale) / scale
    }
    val round = roundAt(2)_
}



