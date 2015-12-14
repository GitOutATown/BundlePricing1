package bundlepricing.lab2.app

trait Priced { def price: Double }

/** API Create catalog item. */
case class Item(
    identity: String,
    price: Double
) extends Priced { require(price > 0) }

private[app] case class Cart(
    items: List[Priced],
    total: Double = 0.0
)

object CartService {
    
    import scala.concurrent.Future
    import scala.concurrent.ExecutionContext.Implicits.global
    import Util.round
    
    ///// API Cart /////
    
    def getCart() = Cart(Nil)
    
    /** 
     *  Single item 
     */
    def addToCart(item: Item, cart: Cart): Cart = {
        cart.copy(items = item :: cart.items)
    }
    
    /** 
     *  Multiple of same item 
     */
    def addToCart(item: Item, qty: Int, cart: Cart): Cart = {
        if(qty == 0) cart
        else addToCart(item, qty - 1, cart.copy(items = item :: cart.items))
    }
    
    /** 
     *  Multiple items, each with variable quanties 
     */
    @annotation.tailrec
    def addToCart(items: List[(Item, Int)], cart: Cart): Cart = items match {
        case Nil => cart
        case (item, qty) :: tail => addToCart(tail, addToCart(item, qty, cart))
    }
    
    /** 
     *  API Calculates minimum cart total per best combination of bundle 
     *  discounts. 
     */
    def checkout(cart: Cart, bundles: List[Bundle]): Future[Cart] = Future {
        val applicableBundles = bundles.filter(bundleMatch(cart, _))
        val bundlePerms = (applicableBundles.permutations).toList
        // Each iteration has original cart but different bundle order. Every possible sequence of bundles is tried.
        val allCartVersions = bundlePerms.map(applyBundles(cart, _))
        val minCart = allCartVersions.reduceLeft(minTotal)
        minCart
    }
    
    /** 
     *  Initial filter for cart relevancy based on adequate item qty
     *  without consideration for overlap with other bundles.
     */
    private[app] def bundleMatch(cart: Cart, bundle: Bundle): Boolean = {
        val required = bundle.appliedTo ++ bundle.addQualifier
        required.forall{ bundleItem =>
            cart.items.count(_ == bundleItem.item) >= bundleItem.qty }
    }
    
    /** 
     *  Matches and replaces loose cart items with BundleItems (item qty 
     *  with discount). If bundle can't be applied, leaves cart items as found.
     */
    private[app] def applyBundles(cart: Cart, bundles: List[Bundle])
        : Cart = bundles match {
        case Nil => cartTotal(cart) // All bundles have been applied or tried.
        case bundle :: tail => applyBundles(applyBundle(cart, bundle), tail)
    }
    
    /** 
     *  Process all BundleItems for this Bundle. 
     */
    private[app] def applyBundle(cart: Cart, bundle: Bundle): Cart = {
        // Recursive
        def inner(
            items: List[Priced], bundleItems: List[BundleItem]
        ): List[Priced] = bundleItems match {
            case Nil => // Successful application of all BundleItems. Any failure shorcuts to outer, returning original cart.items.
                val appliedBundle = applyDiscount(bundle) 
                appliedBundle :: items 
            case bundleItem :: tail => {
                val targetItem = bundleItem.item
                val count = 0
                val targetQty = bundleItem.qty
                val acc = List[Priced]()
                
                // TODO: Replace tuple with BundleContext case class
                val context = (acc, count, targetQty, targetItem)
                
                // Match and remove loose cart items that exist in BundleItem.
                val result = items.foldRight(context)((item, context) => {
                    // if count < targetQty filter out targetItem
                    if(item == targetItem && context._2 < context._3)
                        (context._1, context._2 + 1, context._3, context._4)
                    else (item :: context._1, context._2, context._3, context._4)
                })
                
                // if count == targetQty we have successful application of BundleItem, so recurse with bundle items removed from loose cart items.
                if(result._2 == result._3) inner(result._1, tail)
                // else unsuccessful application of BundleItem so return to outer with original cart items.
                else cart.items
            }
        } // end inner
        
        // Start recursion of BundleItems for this Bundle.
        val appliedItems = inner(cart.items, bundle.appliedTo ++ bundle.addQualifier)
        
        // Return outer. Could be either successful or unsuccessful BundleItem application.
        Cart(appliedItems)
    }
    
    // TODO: Replace with parameterized function instead of hard coded types which have to be referenced and maintained in two separate places (i.e. case class and this function).
    private[app] def applyDiscount(bundle: Bundle): Bundle = {
        
        val originalPrice = (for{
            appliedTo <- bundle.appliedTo
        } yield appliedTo.item.price * appliedTo.qty).sum
        
        val addQualifPrice = (for{
            addQualifier <- bundle.addQualifier
            if(addQualifier != null)
        } yield addQualifier.item.price * addQualifier.qty).sum
        
        val newBundle = bundle.copy(
            beforeDiscount = round(originalPrice + addQualifPrice))
        
        bundle.discount match {
            case pctOff: PercentOff =>
                val percentOff = originalPrice * pctOff.pct
                newBundle.copy(price = round((originalPrice - percentOff) + addQualifPrice))
                
            case flatPrice: BundlePrice => 
                newBundle.copy(price = round(flatPrice.flat + addQualifPrice))
            
            case fewerQty: ForPriceOf =>
                val fewerPrice = (for(
                    bundleItem <- bundle.appliedTo  
                ) yield bundleItem.item.price * fewerQty.qty).sum
                newBundle.copy(price = fewerPrice + addQualifPrice)
        }
    } // end applyDiscount
    
    private[app] def cartTotal(cart: Cart): Cart = {
        val result = cart.items.foldRight(0.0)((item, acc) => item.price + acc)
        cart.copy(total = round(result))
    }
    
    private[app] def minTotal(a: Cart, b: Cart): Cart = if(a.total < b.total) a else b 
}

