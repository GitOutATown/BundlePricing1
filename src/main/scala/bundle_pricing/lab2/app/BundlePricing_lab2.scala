package bundle_pricing.lab2.app

/**
 * TODO:
 * Pattern match on bundles: identify the constituent items.
 * Calculate minPrice 
 * Validators: Option/Either/Try
 * Item factory with unique id generator
 * Protect API, case classes with private declarations, factories
 * Print all line items and discounts
 * Asynchronous calls
 * Tests
 */

trait Bundleable
trait Discount

private[app] case class StoreItem(
    id: String,
    price: Double
)

private[app] case class CartItem(
    item: StoreItem,
    qty: Int
)

case class BundleItem(
    item: StoreItem,
    qty: Int
) extends Bundleable

case class Cart(
    items: List[CartItem]
)

object Item {
    // InventoryItem factory
    def apply(id: String, price: Double, qty: Int): CartItem = {
        val item = StoreItem(id, price)
        CartItem(item, qty)
    }
}

object CartService {
    def addItem(item: CartItem, cart: Cart): Cart = {
        cart.copy(items = item :: cart.items)
    }
    
    def addItems(item: CartItem, qty: Int, cart: Cart): Cart = {
        val items = List.fill(qty)(item)
        cart.copy(items = items ++ cart.items)
    }
    
    def addItems(items: List[CartItem], cart: Cart): Cart =
        cart.copy(items = cart.items ++ items)
    
    // Calculates minimum price per bundle discounts
    // Prints line items and discounts applied
    def checkout(cart: Cart): Double = {
        0 // TODO: STUB
    }
    
    def getCart() = Cart(Nil)
}

case class PercentOff(pct: Double) extends Discount
case class FlatPrice(flat: Double) extends Discount

// Aggregation of required items, with applied discount
case class Bundle(
    qualifier: List[Bundleable],
    discount: AppliedHow
) extends Bundleable

// What kind of discount applied to what
case class AppliedHow(
    discount: Discount,
    appliedTo: Bundleable
)

// Create Bundles
object BundleService {
    
    // BundleItem has quantity
    def qtyForPrice(qualifier: BundleItem, price: Double): Bundle = {
        val discount = AppliedHow(FlatPrice(price), qualifier)
        Bundle(List(qualifier), discount)
    }
    
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
