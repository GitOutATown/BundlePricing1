package bundle_pricing.lab2.app

sealed trait PricedItem { def price: Double }

case class Item(
    identity: String,
    price: Double
) extends PricedItem

private[app] case class BundleItem(
    item: Item,
    qty: Int
)

private[app] case class AppliedBundleItem(
    item: BundleItem,
    discount: Discount,
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
        val cartTotals = allCartVersions.map(cartTotal(_)) // TODO: Move this to applyBundles
        val minCart = cartTotals.reduceLeft(minTotal)
        minCart
    }
    
    /* TODO: Be careful: Cart.items are now PricedItem, which allows for 
     * AppliedBundleItems. This is a risk!
     */
    // Purpose: initial filter for cart relevancy based on adequate items
    // without consideration for overlap with other bundles.
    def bundleMatch(cart: Cart, bundle: Bundle): Boolean = {
        val required = bundle.appliedTo ++ bundle.addQualifier
        required.forall{ bundleItem =>
            cart.items.count(_ == bundleItem.item) >= bundleItem.qty }
    }
    
    /** Precicely matches and replaces loose cart items with BundleItems (item qty 
     *  with discount). If bundle can't be applied, leaves cart items as found.
     */
    def applyBundles(cart: Cart, bundles: List[Bundle]): Cart = bundles match {
        case Nil => cart // All bundles have been applied or tried.
        case bundle :: tail => applyBundles(applyBundle(cart, bundle), tail)
    }
    
    /** Process all BundleItems for this Bundle. */
    def applyBundle(cart: Cart, bundle: Bundle): Cart = {
        // Recursive
        def inner(
            items: List[PricedItem], bundleItems: List[BundleItem]
        ): List[PricedItem] = bundleItems match {
            case Nil => items // Successful application of all BundleItems. Any failure shorcuts to outer, returning original cart.items.
            case bundleItem :: tail => {
                val targetItem = bundleItem.item
                val count = 0
                val targetQty = bundleItem.qty
                val acc = List[PricedItem]()
                
                // TODO: Replace tuple with BundleContext case class
                val context = (acc, count, targetQty, targetItem)
                
                // Remove cart items that exist in BundleItem.
                val result = items.foldRight(context)((item, context) => {
                    // TODO: NIX: println("item:" + item + " acc:" + context._1 + " count:" + context._2 + " limit:" + context._3)
                    // if count <= targetQty filter out targetItem
                    if(item == targetItem && context._2 < context._3)
                        (context._1, context._2 + 1, context._3, context._4)
                    else (item :: context._1, context._2, context._3, context._4)
                })
                
                // if count == targetQty we have successful application of BundleItem, so recurse with BundleItem replacing filtered-out loose items.
                if(result._2 == result._3) {
                    val addedBundleItem = 
                        applyDiscount(bundleItem, bundle.discount) :: result._1
                    inner(addedBundleItem, tail)
                }
                // else unsuccessful application of BundleItem so return to outer with original cart.items
                else cart.items
            }
        } // end inner
        
        // Start recursion of BundleItems for this Bundle.
        val appliedItems = inner(cart.items, bundle.appliedTo)
        
        // Return outer. Could be either successful or unsuccessful BundleItem application.
        Cart(appliedItems)
    }
    
    // TODO: Replace with parameterized function instead of hard coded types which have to be referenced and maintained in two separate places (i.e. case class and this function).
    def applyDiscount(
        bundleItem: BundleItem, discount: Discount
    ): AppliedBundleItem = discount match {
        
        case pctOff: PercentOff =>
            val originalPrice = bundleItem.item.price * bundleItem.qty
            val percentOff = originalPrice * pctOff.pct
            AppliedBundleItem(bundleItem, discount, originalPrice - percentOff)
            
        case flatPrice: BundlePrice => 
            AppliedBundleItem(bundleItem, discount, flatPrice.flat)
            
        case fewerQty: ForPriceOf => 
            AppliedBundleItem(
                bundleItem, discount, bundleItem.item.price * fewerQty.qty)
    }
    
    def cartTotal(cart: Cart): Cart = {
        val result = cart.items.foldRight(0.0)((item, acc) => item.price + acc)
        cart.copy(total = round(result))
    }
    
    def minTotal(a: Cart, b: Cart): Cart = if(a.total < b.total) a else b
}

private[app] sealed trait Discount
private[app] case class PercentOff(pct: Double) extends Discount
private[app] case class BundlePrice(flat: Double) extends Discount
private[app] case class ForPriceOf(qty: Int) extends Discount

// Aggregation of required items, with applied discount
private[app] case class Bundle(
    discount: Discount,
    appliedTo: List[BundleItem],
    addQualifier: List[BundleItem],
    description: String
)

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
     *  TODO: Multiple items with specific quantities, each for percent off of 
     *  their list price.
     */ 
    def percentPrice(discountItem: Item, qty: Int, pctOff: Double)
                    (addQualifier: (Item, Int)*)
                    (description: String): Bundle = {
        val discount = PercentOff(pctOff)
        val appliedTo = BundleItem(discountItem, qty)
        val addQualifier_ = addQualifier.toList.map{
            case (item, qty) => BundleItem(item, qty)
        }
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
            case (item, qty) => BundleItem(item, qty)
        }
        val addQualifier_ = addQualifier.toList.map{
            case (item, qty) => BundleItem(item, qty)
        }
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



