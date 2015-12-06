package bundle_pricing.lab2.app

/**
 * TODO:
 * Pattern match on bundles: identify the constituent items.
 * Calculate minPrice 
 * Validators: Option/Either/Try
 * Protect API, case classes with private declarations, factories
 * Print all line items and discounts
 * Asynchronous calls
 * Tests
 * Documentation comments
 */

//trait Bundleable
trait Discount

case class Item(
    identity: String,
    price: Double
)

private[app] case class BundleItem(
    item: Item,
    qty: Int
) //extends Bundleable

private[app] case class Cart(
    items: List[Item]
)

object CartService {
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
    def addToCart(items: List[(Item, Int)], cart: Cart): Cart = items match {
        case Nil => cart
        case (item, qty) :: tail => addToCart(tail, addToCart(item, qty, cart))
    }
    
    // Calculates minimum price per bundle discounts
    // Prints line items and discounts applied
    def checkout(cart: Cart): Double = {
        0 // TODO: STUB
    }
    
    // TODO: Make private
    def bundleMatch(cart: Cart, bundle: Bundle): Boolean = {
        val cartItems = cart.items
        println("-->bundleMatch cartItems: " + cartItems)
        println("-->bundleMatch bundle.qualifier: " + bundle.qualifier)
        // All BundleItems in qualifier list must exist in cart.
        bundle.qualifier match {
            case Nil => {
                println("-->bundleMatch Nil")
                true // No failures found
            }
            // TODO: THIS MUST RECURSE!!!!!!!
            case head :: tail => head match {
                case bundleItem: BundleItem => {
                    println("-->bundleMatch head: " + bundleItem)
                    // There is just this generalized pattern:
                    // i.e. match BundleItem quantity with cart quantity
                    val qualifierCount = cartItems.count(_ == bundleItem.item)
                    println("-->bundleMatch qualifierCount: " + qualifierCount)
                    val result = qualifierCount >= bundleItem.qty
                    println("-->bundleMatch result<1>: " + result)
                    result
                }
                /*case b: Bundle => {
                    println("-->bundleMatch Bundle: " + b)
                }*/
            }
        }
    }
}

/*
 * TODO: How bout this for an idea of how to do generalized
 * function parameterizaton for discounts:
 * map price to price--the function implements how, so
 * f: A => B
 * or
 * f: Double => Double // ie. price
 * This could be applicable for either flat price or percentage
 * but what about m for price of n pricing?
 * That doesn't seem to fit f: A => B
 * Well, it does! It doesn't fit f: Double => Double
 * but it fits f: A => B, which in m for n pricing would be f: Int => Int
 * But how then does the mapping work? How does it know which fields to apply to?
 * This is where I really need to keep studying FP in the red book.
 */

/*
 * Ok, I think I have a plan.
 * I basically have 3 days left: Sun, Mon, Tue
 * Sun: Reworking Bundle parsing, i.e. bundleMatch so that it is completely
 * generalized. No conditional sequences of blocks, just one mapping that 
 * takes a passed (parameterized) function that applies the discount.
 */

private[app] case class PercentOff(pct: Double) extends Discount
private[app] case class BundlePrice(flat: Double) extends Discount
private[app] case class ForPriceOfQty(fewer: Int) extends Discount

// Aggregation of required items, with applied discount
private[app] case class Bundle(
    qualifier: List[BundleItem], // Just 
    discount: AppliedHow
) //extends Bundleable

// What kind of discount applied to what
private[app] case class AppliedHow(
    discount: Discount,
    appliedTo: BundleItem
)

// Create Bundles
object BundleService {
    
    /** Factories. BundleItem has required quantity */
    /* TODO: This should be just one generalized factory method. 
     * The Discount trait should be passed in as the differentiator.
     */
    
    def qtyForPrice(item: Item, qty: Int, bundlePrice: Double): Bundle = {
        val qualifier = BundleItem(item, qty)
        val discount = AppliedHow(BundlePrice(bundlePrice), qualifier)
        Bundle(List(qualifier), discount)
    }
    
    def forPriceOfQty(item: Item, more: Int, less: Int): Bundle = {
        val qualifier = BundleItem(item, more)
        val discount = AppliedHow(ForPriceOfQty(less), qualifier)
        Bundle(List(qualifier), discount)
    }
    
    // Factory
    def bundle(
        otherParts: List[BundleItem], // Items with quantities
        discount: Discount, // TODO: This should be a function. Needs parameters.
        appliedTo: BundleItem // Item with quantity, might be same as otherparts.head i.e. otherparts.length = 1
    ): Bundle = {
        // TODO: Verify/Require that appliedTo exists in parts
        val applyHow = AppliedHow(discount, appliedTo)
        val allParts = appliedTo :: otherParts
        Bundle(allParts, applyHow)
    }
}
