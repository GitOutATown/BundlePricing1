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

case class Item(
    identity: String,
    price: Double
)

private[app] case class BundleItem(
    item: Item,
    qty: Int
)

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

trait Discount

// Aggregation of required items, with applied discount
private[app] case class Bundle(
    qualifier: List[BundleItem], 
    discount: Discount
)

// Create Bundles
object BundleService {
    
    case class PercentOff(pct: Double, item: BundleItem) extends Discount
    case class BundlePrice(flat: Double, item: BundleItem) extends Discount
    case class ForPriceOf(qty: Int, item: BundleItem) extends Discount
        
    /** Factories. BundleItem has required quantity */
    /* TODO: This should be just one generalized factory method. 
     * The Discount trait should be passed in as the differentiator.
     */
    
    /*def qtyForPrice(item: Item, qty: Int, bundlePrice: Double): Bundle = {
        val qualifier = BundleItem(item, qty)
        val discount = AppliedHow(BundlePrice(bundlePrice), qualifier)
        Bundle(List(qualifier), discount)
    }
    
    def forPriceOfQty(item: Item, more: Int, less: Int): Bundle = {
        val qualifier = BundleItem(item, more)
        val discount = AppliedHow(ForPriceOfQty(less), qualifier)
        Bundle(List(qualifier), discount)
    }*/
    
    // Factory
    def bundle(
        discount: Discount,
        tupleItems: (Item, Int)* // Items with quantities
    ): Bundle = {
        val bundleItems = tupleItems.map{
            case (item, qty) => BundleItem(item, qty)
        }
        Bundle(bundleItems.toList, discount)
    }
}
