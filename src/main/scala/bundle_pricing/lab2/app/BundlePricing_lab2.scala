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

/* TODO: NIX comment.
 * I like this--it does get at capturing the applied price, but I'm affraid
 * that the price should actually be calculated at the item level. It really
 * needs to be both! Ok, I'm going to make an executive decision here and
 * not do that! Time is running out! I'm only going to capture the items, which
 * is easy, because Bundle item has already done that (I just need to remove
 * them from the cart's item list. But I'm only going to aggregate the price
 * per the discount at the Bundle Item level, not at the individual item!
 */
private[app] case class AppliedBundleItem(
    item: BundleItem,
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
    
    // Calculates minimum cart total per bundle discounts
    def checkout(cart: Cart, bundles: List[Bundle]): Cart = {
        val applicableBundles = bundles.filter(bundleMatch(cart, _))
        val bundlePerms = (applicableBundles.permutations).toList
        
        /* Each iteration has original cart but different bundle order.
         * Every possible sequence of bundle captures are tried. */
        val allCartVersions = bundlePerms.map(applyBundles(cart, _))
        val cartTotals = allCartVersions.map(cartTotal(_)) // TODO: Move this to applyBundles
        val minCart = cartTotals.reduceLeft(minTotal)
        minCart
    }
    
    def bundleMatch(cart: Cart, bundle: Bundle): Boolean = {
        val required = bundle.appliedTo ++ bundle.addQualifier
        required.forall{ bundleItem =>
            cart.items.count(_ == bundleItem.item) >= bundleItem.qty }
    }
    
    // Recursive
    def applyBundles(cart: Cart, bundles: List[Bundle]): Cart = {
        def inner(
            items: List[Item], bundles: List[Bundle]
        ): List[Item] = bundles match {
            case Nil => getCart ???
            case bundle :: tail => {
                // List of BundleItems that the Discount is applied to.
                val bundleItems = bundle.appliedTo
                /* What do I need/want to do here? BundleItems are the
                 * specific items (with qty) that I need to capture from
                 * the available items in the cart. So capture them!!
                 * What about using a method similar to bundleMatch?
                 * Yes--here's something useful: val required in bundleMatch
                 * is a List[BundleItem] and that's exactly what bundleItems
                 * is here! So...
                 * Also, there needs to be another entity that serves to 
                 * encapsulate the successfully captured items of the bundle.
                 * But can't the existing BundleItem do that? No, it doesn't
                 * have a price. But how exactly am I encapsulating and computing
                 * the discounted prices in the bundle????
                 */
            } 
        }
        val finalItems = inner(cart.items, bundles)
        Cart(finalItems)
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



