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
        // Identify bundles
        // Qualify bundles
        // Apply bundles in all cart combinations
        // Select bundle config with lowest cart total cost
        
        0 // TODO: STUB
    }
    
    def bundleMatch(cart: Cart, bundle: Bundle): Boolean = {
        val cartItems = cart.items
        
        println("-->bundleMatch cartItems: " + cartItems)
        println("-->bundleMatch bundle.appliedTo: " + bundle.appliedTo)
        println("-->bundleMatch bundle.appliedTo: " + bundle.addQualifier)
        
        val required = bundle.appliedTo ++ bundle.addQualifier
        required.forall{ bundleItem =>
            cartItems.count(_ == bundleItem.item) >= bundleItem.qty }
    }
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



