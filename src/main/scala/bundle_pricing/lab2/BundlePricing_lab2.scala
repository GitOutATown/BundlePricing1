package bundle_pricing.lab2

/**
 * TODO:
 * Pattern match on bundles: identify the constituent items.
 * Calculate minPrice 
 * Validators: Option/Either/Try
 * Print all line items and discounts
 * Asynchronous calls
 * Tests
 */

case class Item(
    id: String,
    price: Double,
    qty: Int = 1
) extends Bundleable

case class Cart(
    items: List[Item]
)

object CartService {
    def addItem(item: Item, cart: Cart): Cart = {
        cart.copy(items = item :: cart.items)
    }
    
    def addItems(item: Item, qty: Int, cart: Cart): Cart = {
        val items = List.fill(qty)(item)
        cart.copy(items = items ++ cart.items)
    }
    
    // Calculates minimum price per bundle discounts
    // Prints line items and discounts applied
    def checkout(cart: Cart): Double = {
        0 // TODO: STUB
    }
}

trait Bundleable
trait Discount

case class PercentOff(pct: Double) extends Discount
case class FlatPrice(flat: Double) extends Discount

// Aggregation of required items, with applied discount
case class Bundle(
    qualifier: List[Bundleable],
    discount: AppliedHow,
    price: Double = 0
) extends Bundleable

// What kind of discount applied to what
case class AppliedHow(
    discount: Discount,
    grouping: Bundleable
)

// Create Bundles
object BundleService {
    
    // Item has quantity
    def qtyForPrice(qualifier: Item, price: Double): Bundle = {
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

