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
    def addToCart(storeItem: StoreItem, qty: Int, cart: Cart): Cart = {
        val cartItem = CartItem(storeItem, qty)
        cart.copy(items = cartItem :: cart.items)
    }
    
    def addToCart(items: List[(StoreItem, Int)], cart: Cart): Cart = items match {
        case Nil => cart
        case (item, qty) :: t => addToCart(t, addToCart(item, qty, cart))
    }
    
    /*def addItems(items: List[CartItem], cart: Cart): Cart =
        cart.copy(items = cart.items ++ items)*/
    
    // Calculates minimum price per bundle discounts
    // Prints line items and discounts applied
    def checkout(cart: Cart): Double = {
        0 // TODO: STUB
    }
    
    def getCart() = Cart(Nil)
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
    /*def qtyForPrice(identity: String, qty: Int, bundlePrice: Double): Bundle = {
        val item = StoreItem(identity)
        val discount = AppliedHow(BundlePrice(bundlePrice), qualifier)
        Bundle(List(qualifier), discount)
    }*/
    
    /*def qtyForPrice(qualifier: BundleItem, price: Double): Bundle = {
        val discount = AppliedHow(FlatPrice(price), qualifier)
        Bundle(List(qualifier), discount)
    }*/
    
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
