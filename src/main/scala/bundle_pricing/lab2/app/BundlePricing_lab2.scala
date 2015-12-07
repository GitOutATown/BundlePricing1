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
    
    /*
     *  TODO: Be careful: Cart.items are now PricedItem, which allows for 
     *  AppliedBundleItems. This is a risk!
     */
    def bundleMatch(cart: Cart, bundle: Bundle): Boolean = {
        val required = bundle.appliedTo ++ bundle.addQualifier
        required.forall{ bundleItem =>
            cart.items.count(_ == bundleItem.item) >= bundleItem.qty }
    }
    
    // Recursive
    def applyBundles(cart: Cart, bundles: List[Bundle]): Cart = {
        
        def inner(
            items: List[PricedItem], bundles: List[Bundle]
        ): List[PricedItem] = bundles match {
            case Nil => items // All bundles have been applied or tried
            case bundle :: tail => {
                
                /*
                 * Ok, I'm still qualifying here, sort of similar to
                 * bundleMatch, but precicely matches and removes loose
                 * items from cart if all items are able to be captured,
                 * otherwise doesn't apply bundle (i.e. leaves cart.items
                 * as they are.)
                 */
                
                applyBundle(cart, bundle) // TODO: Should this be List[PricedItems]?
                
                List(Item("STUB", 0.0)) // STUB
            } 
        }
        
        val finalItems = inner(cart.items, bundles)
        Cart(finalItems)
    } // end - applyBundles
    
    // Process all BundleItems
    def applyBundle(cart: Cart, bundle: Bundle): Cart = {
        // Recursive
        def inner(
            items: List[PricedItem], bundleItems: List[BundleItem]
        ): List[PricedItem] = bundleItems match {
            case Nil => items // Successful application of all BundleItems. Any failure shorcuts to outer returning original cart.items.
            case bundleItem :: tail => {
                val targetItem = bundleItem.item
                val count = 0
                val targetQty = bundleItem.qty
                val acc = List[PricedItem]()
                val context = (acc, count, targetQty, targetItem) // TODO: Make BundleContext case class
                
                // Remove cart items that exist in BundleItem
                val result = items.foldRight(context)((item, context) => {
                    // TODO: NIX: println("item:" + item + " acc:" + context._1 + " count:" + context._2 + " limit:" + context._3)
                    // if (count <= targetQty)
                    if(item == targetItem && context._2 < context._3)
                        (context._1, context._2 + 1, context._3, context._4)
                    else (item :: context._1, context._2, context._3, context._4)
                })
                
                // if(count == targetQty) Successful application of BundleItem, 
                // so recurse with filtered out cart.items and their BundleItem replacement.
                if(result._2 == result._3) {
                    val bundleTotal = 0.0 // TODO: STUB, should be method that applies discount to bundle, but that is why ALL BundleItems must be applied in this method. 
                    AppliedBundleItem(bundleItem, bundleTotal) :: items
                    inner(result._1, tail)
                }
                // else return to outer with original cart.items
                else cart.items
            }
        } // end inner
        
        // TODO: Where (and how) should the Bundle Discounts be applied?
        
        val appliedItems = inner(cart.items, bundle.appliedTo)
        Cart(appliedItems)
        
    } // end applyBundle
    
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



