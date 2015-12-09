package bundle_pricing.lab2.app

sealed trait Priced { def price: Double }

/** API Create catalog item. */
case class Item(
    identity: String,
    price: Double
) extends Priced

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
        // Each iteration has original cart but different bundle order. Every possible sequence of bundles are tried.
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

    // Side-effect. TODO: Move this to separate file.
    def printReceipt(cart: Cart) {
        println("-------RECEIPT-------")
        cart.items.foreach { pricedItem => pricedItem match {
            case item: Item =>
                val identity = item.identity
                val price = round(item.price)
                println(s"ITEM:\t$identity\t$price")
                
            case bundle: Bundle =>
                println(".....................")
                println(s"BUNDLE:")
                
                for{
                    bundleItem <- bundle.appliedTo
                    identity = bundleItem.item.identity
                    itemPrice = round(bundleItem.item.price)
                    qty = bundleItem.qty
                } yield println(s"$identity\tQTY:\t$qty")
                
                for{
                    addQualifier <- bundle.addQualifier
                } yield {
                    if(addQualifier != null) {
                        val aqIdent = addQualifier.item.identity
                        val aqQty = addQualifier.qty
                        println(s"$aqIdent\tQTY:\t$aqQty")
                    }
                }
                
                val regPrice = round(bundle.beforeDiscount)
                val bundPrice = round(bundle.price)
                val savings = round(regPrice - bundPrice)
                val description = bundle.description
                println(s"$description\t$bundPrice\nSAVINGS:\t$savings")
                println(".....................")
        }}
        val total = cart.total
        println(s"TOTAL\t\t$total")
        println("--------------------")
    }
}

private[app] sealed trait Discount
private[app] case class PercentOff(pct: Double) extends Discount
private[app] case class BundlePrice(flat: Double) extends Discount
private[app] case class ForPriceOf(qty: Int) extends Discount

/** 
 *  Aggregation of required items, with applied discount.
 *  Bundle price may be result of either percentage or flat or N for M discounts, 
 *  but is applied as an aggregate to all BundleItems. Current model, however, 
 *  can only hold multiple appliedTo BundleItems for flat price discount.
 *  
 *  @param discount is the Discount type (i.e. percent off, flat rate, N for M)
 *  
 *  @param appliedTo are the items whose prices are affected by the discount. Currently
 *  only BundlePrice discount can support multiple appliedTo BundleItems.
 *  
 *  @param addQualifier is not directly affected by the discount. If extant, it 
 *  is a bundle requirement. Its regular price is included in the aggregated 
 *  bundle price.
 *  
 *  @param description is the description of the bundle discount (i.e. the type,
 *  the items which directly receive the discount, and the other required items.
 *  
 *  @param beforeDiscount is aggregated bundle price before discount applied to 
 *  appliedTo items.
 *  
 *  @param price is the aggregated price for all items in the bundle
 *  including appliedTo and addQualifier. bundlePrice, at this iterative
 *  stage, is primarily a mechanism for tallying the cart total. It also 
 *  indicates the value of the bundle and is used to calculate savings.
 */
private[app] case class Bundle(
    discount: Discount,
    appliedTo: List[BundleItem],
    addQualifier: List[BundleItem],
    description: String,
    beforeDiscount: Double = 0.0,
    price: Double = 0.0
) extends Priced

private[app] case class BundleItem(
    item: Item,
    qty: Int
)

object BundleService {
    
    ///// API Bundle factories /////
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
     *  TODO: Multiple items with various quantities, each for percent off of 
     *  their list price.
     */ 
    def percentPrice(discountItem: Item, qty: Int, pctOff: Double)
                    (addQualifier: (Item, Int)*)
                    (description: String): Bundle = {
        val discount = PercentOff(pctOff)
        val appliedTo = BundleItem(discountItem, qty)
        val addQualifier_ = addQualifier.toList.map{
            case (item, qty) => BundleItem(item, qty)}
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
            case (item, qty) => BundleItem(item, qty)}
        val addQualifier_ = addQualifier.toList.map{
            case (item, qty) => BundleItem(item, qty)}
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



