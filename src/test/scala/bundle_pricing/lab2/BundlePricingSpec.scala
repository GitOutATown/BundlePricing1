package bundle_pricing.lab2

import org.scalatest._
import org.scalatest.concurrent.ScalaFutures

import bundle_pricing.lab2.app._
import bundle_pricing.lab2.app.BundlePrice
import bundle_pricing.lab2.app.CartService._
import bundle_pricing.lab2.app.BundleService._

import scala.concurrent.Future
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global

class BundlePricingSpec extends UnitSpec {
    
    "The items in a new cart, instantiated with getCart," should "be Nil" in {
        assert(getCart.items == Nil)
    }
    
    "A cart containing items" should "total 0 before it has gone through checkout" in {
        val itemList = List((Item("I1", 1.50), 3), (Item("I2", 2.00), 2), (Item("I3", 3.25), 1))
        val cart = addToCart(itemList, getCart)
        assert(cart.total == 0)
    }
    
    "A cart containing items but no bundles" should "total to the sum of all" + 
        "item prices after going through checkout" in {
        
        val itemList = List((Item("I1", 1.50), 3), (Item("I2", 2.00), 2), (Item("I3", 3.25), 1))
        val itemPriceSum = itemList.foldRight(0.0)((tup, acc) => (tup._1.price * tup._2) + acc)
        val manualTotal = (3 * 1.50) + (2 * 2.0) + (1 * 3.25)
        val cart = addToCart(itemList, getCart)
        
        val cartFut = checkout(cart, Nil)
        whenReady(cartFut) { cart =>
            assert(cart.total == itemPriceSum && cart.total == manualTotal)
        }
    }
    
    "The total of a cart with a single flat price bundle that exactly matches " +
        "the cart items" should "equal the bundle price" in {
        
        val numItems = 5
        val bundlePrice_ = 3.00
        val item1 = Item("Item1", 1.00)
        
        val cart = addToCart(item1, numItems, getCart)
        
        val bundle = bundlePrice(bundlePrice_, (item1, numItems))()(
            s"$numItems $item1.identity for $bundlePrice_")
        
        val cartFut = checkout(cart, List(bundle))
        whenReady(cartFut) { cart =>
            assert(cart.total == bundlePrice_)
        }
    }
    
    "The total of a cart with items that match a flat price bundle plus one other" +
        " item " should "equal the bundle price plus the price of the extra item" in {
        
        val numItems = 5
        val bundlePrice_ = 3.00
        val item1 = Item("Item1", 1.00)
        val item2 = Item("Item2", 0.99)
        
        val cart = addToCart(List((item1, numItems), (item2, 1)), getCart)
        
        val bundle = bundlePrice(bundlePrice_, (item1, numItems))()(
            s"$numItems $item1.identity for $bundlePrice_")
        
        val cartFut = checkout(cart, List(bundle))
        whenReady(cartFut) { cart =>
            assert(cart.total == bundlePrice_ + item2.price)
        }
    }
    
    "The total of a cart with a single percent-off-price bundle that exactly " +
        "matches the cart items" should "equal the bundle price" in {
        val numItems = 5
        val percentOffAmt = 0.25
        val item1 = Item("Item1", 1.00)
        val regPrice = (item1.price * numItems)
        val bundlePrice = regPrice - (regPrice * percentOffAmt)
        
        val cart = addToCart(item1, numItems, getCart)
        
        val bundle = percentPrice(item1, numItems, percentOffAmt)()(
            s"$numItems $item1.identity for $percentOffAmt off")
    
        val cartFut = checkout(cart, List(bundle))
        whenReady(cartFut) { cart =>
            assert(cart.total == bundlePrice)
        }
    }
    
    "The total of a cart with items that match a percent-off-price bundle plus one other" +
        " item " should "equal the bundle price plus the price of the extra item" in {
        val numItems = 5
        val percentOffAmt = 0.25
        val item1 = Item("Item1", 1.00)
        val item2 = Item("Item2", 0.99)
        val regPrice = (item1.price * numItems)
        val bundlePrice = regPrice - (regPrice * percentOffAmt)
        
        val cart = addToCart(List((item1, numItems), (item2, 1)), getCart)
        
        val bundle = percentPrice(item1, numItems, percentOffAmt)()(
            s"$numItems $item1.identity for $percentOffAmt off")
    
        val cartFut = checkout(cart, List(bundle))
        whenReady(cartFut) { cart =>
            assert(cart.total == bundlePrice + item2.price)
        }
    }
    
    "The total of a cart with a single N-for-price-of-M bundle that exactly " +
        "matches the cart items" should "equal the bundle price" in {
        
        val nQty = 5
        val mQty = 4
        val item1 = Item("Item1", 1.00)
        val bundlePrice = item1.price * mQty
        
        val cart = addToCart(item1, nQty, getCart)
        
        val bundle = forPriceOfQty(item1, nQty, mQty)()(
            s"$nQty item1.identity for the price of $mQty")
            
        val cartFut = checkout(cart, List(bundle))
        whenReady(cartFut) { cart =>
            assert(cart.total == bundlePrice)
        }
    }
    
    "The total of a cart with items that match an N-for-price-of-M bundle plus one other " +
        "item" should "equal the bundle price plus the price of the extra item" in {
        
        val nQty = 5
        val mQty = 4
        val item1 = Item("Item1", 1.00)
        val item2 = Item("Item2", 0.99)
        val bundlePrice = item1.price * mQty
        
        val cart = addToCart(List((item1, nQty), (item2, 1)), getCart)
        
        val bundle = forPriceOfQty(item1, nQty, mQty)()(
            s"$nQty item1.identity for the price of $mQty")
            
        val cartFut = checkout(cart, List(bundle))
        whenReady(cartFut) { cart =>
            assert(cart.total == bundlePrice + item2.price)
        }
    }
    
    /** Overlap scenarios */
    
    "Given two bundles with overlapping item criteria and a cart with enough " +
    "items to satisfy both individually, but not both together, the applied " +
    "bundle" should "be the one that results in the lowest cart total." in {
        
        val item1 = Item("Item1", 1.00)
        
        val cart = addToCart(item1, 5, getCart)
        
        val bundle1 = bundlePrice(2.50, (item1, 3))()(
            s"3 $item1.identity for $$2.50")
        
        val bundle2 = bundlePrice(3.49, (item1, 4))()(
            s"4 $item1.identity for $$3.49")
            
        val cartBundle1Fut = checkout(cart, List(bundle1))
        whenReady(cartBundle1Fut) { cart =>
            assert(cart.total == 4.50)
        }
        
        val cartBundle2Fut = checkout(cart, List(bundle2))
        whenReady(cartBundle2Fut) { cart =>
            assert(cart.total == 4.49)
        }
        
        val cartBothBundlesFut = checkout(cart, List(bundle1, bundle2))
        whenReady(cartBothBundlesFut) { cart =>
            assert(cart.total == 4.49)
        }
    }
    
    "Given two bundles with overlapping item criteria and a cart with enough " +
    "items to satisfy both individually, but not both together, in this case the percent-price " +
    "bundle" should "be the one that results in the lowest cart total." in {
        
        val item1 = Item("Item1", 1.00)
        
        val cart = addToCart(item1, 5, getCart)
        
        val bundle1 = bundlePrice(2.50, (item1, 3))()(
            s"3 $item1.identity for $$2.50")
        
        val bundle2 = percentPrice(item1, 4, 0.15)()(
            s"4 $item1.identity for 15% off")
            
        val cartBundle1Fut = checkout(cart, List(bundle1))
        whenReady(cartBundle1Fut) { cart =>
            assert(cart.total == 4.50)
        }
        
        val cartBundle2Fut = checkout(cart, List(bundle2))
        whenReady(cartBundle2Fut) { cart =>
            assert(cart.total == 4.40)
        }
        
        val cartBothBundlesFut = checkout(cart, List(bundle1, bundle2))
        whenReady(cartBothBundlesFut) { cart =>
            assert(cart.total == 4.40)
        }
    }
    
    "Given two bundles with overlapping item criteria and a cart with enough " +
    "items to satisfy both individually, but not both together, in this case the flat-price " +
    "bundle" should "be the one that results in the lowest cart total." in {
        
        val item1 = Item("Item1", 1.00)
        
        val cart = addToCart(item1, 5, getCart)
        
        val bundle1 = bundlePrice(2.50, (item1, 3))()(
            s"3 $item1.identity for $$2.50")
        
        val bundle2 = percentPrice(item1, 4, 0.10)()(
            s"4 $item1.identity for 10% off")
            
        val cartBundle1Fut = checkout(cart, List(bundle1))
        whenReady(cartBundle1Fut) { cart =>
            assert(cart.total == 4.50)
        }
        
        val cartBundle2Fut = checkout(cart, List(bundle2))
        whenReady(cartBundle2Fut) { cart =>
            assert(cart.total == 4.60)
        }
        
        val cartBothBundlesFut = checkout(cart, List(bundle1, bundle2))
        whenReady(cartBothBundlesFut) { cart =>
            assert(cart.total == 4.50)
        }
    }
    
    /** Additional qualifier item scenarios */
    
    "An additional qualifier item" should "exist in the cart for the bundle that requires it to be applied" in {
        val butterStick = Item("Butter Stick", 1.00)
        val bread = Item("3 Seed Bread", 3.99)
        
        val breadAndButterBundle =
        forPriceOfQty(butterStick, 2, 1)((bread, 1))(
            "Bread with 2 sticks butter, get second stick free.")

        val cart1 = addToCart(List((butterStick, 2)), getCart)
        val cart2 = addToCart(List((butterStick, 2), (bread, 1)), getCart)
        
        val checkoutCart1 = checkout(cart1, List(breadAndButterBundle))
        val checkoutCart2 = checkout(cart2, List(breadAndButterBundle))
        
        whenReady(checkoutCart1) { cart =>
            assert(cart.total == butterStick.price * 2)
        }
        
        whenReady(checkoutCart2) { cart =>
            assert(cart.total == (butterStick.price * 1) + bread.price)
        }
    }
    
    Thread.sleep(500) // Don't immediately terminate the class. Let the Futures receive their callbacks.
}

class BundlePricingSuite1 extends FunSuite {
    
    test("An item instantiated with an identity of \"One\" should return identity \"One\".") {
        assert(Item("One", 1.0).identity == "One")
    }
    
    test("An item instantiated with a price of 2.59 should return price 2.59.") {
        assert(Item("One", 2.59).price == 2.59)
    }
}




