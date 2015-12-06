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

trait Bundleable
trait Discount

case class Item(
    identity: String,
    price: Double
)

private[app] case class BundleItem(
    item: Item,
    qty: Int
) extends Bundleable

private[app] case class Cart(
    items: List[Item]
)

object CartService {
    def getCart() = Cart(Nil)
    
    def addToCart(item: Item, cart: Cart): Cart = {
        cart.copy(items = item :: cart.items)
    }
    
    def addToCart(item: Item, qty: Int, cart: Cart): Cart = {
        if(qty == 0) cart
        else addToCart(item, qty - 1, cart.copy(items = item :: cart.items))
    }
    
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
    /*def bundleMatch(cart: Cart, bundle: Bundle): Boolean = {
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
            case bundleable :: tail => bundleable match {
                case head: BundleItem => {
                    println("-->bundleMatch head: " + head)
                    // match BundleItem quantity with cart quantity
                    val qualifierCount = cartItems.count(_.item != head.item)
                    println("-->bundleMatch qualifierCount: " + qualifierCount)
                    val result = head.qty >= qualifierCount
                    println("-->bundleMatch result<1>: " + result)
                    result
                }
                case b: Bundle => {
                    println("-->bundleMatch Bundle: " + b)
                }
            }
        }
        
        //STUB
        true
    }*/
}

private[app] case class PercentOff(pct: Double) extends Discount
private[app] case class BundlePrice(flat: Double) extends Discount

// Aggregation of required items, with applied discount
private[app] case class Bundle(
    qualifier: List[Bundleable],
    discount: AppliedHow
) extends Bundleable

// What kind of discount applied to what
private[app] case class AppliedHow(
    discount: Discount,
    appliedTo: Bundleable
)

// Create Bundles
object BundleService {
    
    // BundleItem has quantity
    // Factory
    def qtyForPrice(item: Item, qty: Int, bundlePrice: Double): Bundle = {
        val qualifier = BundleItem(item, qty)
        val discount = AppliedHow(BundlePrice(bundlePrice), qualifier)
        Bundle(List(qualifier), discount)
    }
    
    // Factory
    def multiPart(
        otherParts: List[Bundleable],
        discount: Discount,
        appliedTo: Bundleable
    ): Bundle = {
        // TODO: Verify/Require that appliedTo exists in parts
        val applyHow = AppliedHow(discount, appliedTo)
        val allParts = appliedTo :: otherParts
        Bundle(allParts, applyHow)
    }
}
