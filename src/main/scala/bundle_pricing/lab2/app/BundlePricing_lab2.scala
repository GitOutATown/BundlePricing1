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

case class StoreItem(
    identity: String,
    price: Double
)

private[app] case class CartItem(
    item: StoreItem,
    qty: Int
)

private[app] case class BundleItem(
    item: StoreItem,
    qty: Int
) extends Bundleable

private[app] case class Cart(
    items: List[CartItem]
)

object CartService {
    def getCart() = Cart(Nil)
    
    def addToCart(storeItem: StoreItem, qty: Int, cart: Cart): Cart = {
        val cartItem = CartItem(storeItem, qty)
        cart.copy(items = cartItem :: cart.items)
    }
    
    def addToCart(items: List[(StoreItem, Int)], cart: Cart): Cart = items match {
        case Nil => cart
        case (item, qty) :: tail => addToCart(tail, addToCart(item, qty, cart))
    }
    
    // Calculates minimum price per bundle discounts
    // Prints line items and discounts applied
    def checkout(cart: Cart): Double = {
        0 // TODO: STUB
    }
    
    // TODO: Make private
    def matchBundle(cart: Cart, bundle: Bundle): Boolean = {
        val items = cart.items
        
        // All in qualifier list must equate to true, i.e. exist in cart
        bundle.qualifier match {
            case bundleable :: t => bundleable match {
                case b: BundleItem => {
                    val qualifierCount = items.count { x => x.item == b.item }
                    b.qty >= qualifierCount
                }
                case b: Bundle => b
            }
        }
        
        //STUB
        true
    }
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
    def qtyForPrice(item: StoreItem, qty: Int, bundlePrice: Double): Bundle = {
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
