package bundle_pricing.lab2

/**
 * TODO: Validators: Option/Either/Try
 * asynchronous
 * tests
 */

case class Item(
    id: String,
    price: Double
)

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
        0 // STUB
    }
}

trait Bundle
trait Discount

case class PercentOff(pct: Double) extends Discount
case class FlatPrice(flat: Double) extends Discount

case class BundlePart(
    item: Item,
    qty: Int
) extends Bundle

// Aggregation of required items, with applied discount
case class BundleWhole(
    qualifiers: List[BundlePart],
    discount: AppliedHow,
    price: Double = 0
) extends Bundle

// What kind of discount applied to what
case class AppliedHow(
    disc: Discount,
    grouping: Bundle
)

// Create Bundles
object BundleService {
    def qtyForPrice(item: Item, qty: Int, price: Double): Bundle = {
        val part = BundlePart(item, qty)
        val qualifier = List(part)
        val discount = AppliedHow(FlatPrice(price), part)
        BundleWhole(qualifier, discount)
    }
    
    def multiPart(
        allParts: List[BundlePart],
        discount: Discount,
        appliedTo: BundlePart
    ): Bundle = {
        // TODO: Verify/Require that appliedTo exists in parts
        val appHow = AppliedHow(discount, appliedTo)
        BundleWhole(allParts, appHow)
    }
}

