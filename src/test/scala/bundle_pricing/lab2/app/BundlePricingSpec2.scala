package bundle_pricing.lab2.app

import org.scalatest._
import org.scalatest.concurrent.ScalaFutures

import bundle_pricing.lab2.app._
import bundle_pricing.lab2.app.CartService._
import bundle_pricing.lab2.app.BundleService._

import scala.concurrent.Future
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global

class BundlePricingSpec2 extends UnitSpec {
    
    "Cart total from checkout with multiple bundles" should "equal lowest " +
    "total of all individually processed bundle combinations (permutations)" in {

        val i1 = Item("ItemOne", 1.0)
        val i2 = Item("ItemTwo", 2.0)
        val i3 = Item("ItemThree", 2.0)
        val addQual = Item("SomethingExtra", 1.0)
    
        val shoppingList = List((i1, 4), (i2, 3), (i3, 3), (addQual, 1))
        
        val cart = addToCart(shoppingList, getCart)
        
        val flatPrice2 = bundlePrice(1.75, (i2, 2))()("2 i2 for $1.75")
        val flatPrice3 = bundlePrice(4.00, (i1, 2),(i3, 2))()("2 i1 and 2 i3 for $4.00")
        val pctOff = percentPrice(i1, 4, 0.5)()("50% off 4 i1")
        val fourForOne = forPriceOfQty(i1, 4, 1)()_
        val dealOfTheDay = fourForOne("i1 4 for price of 1!")
        
        val expectedMinCartTotal = 11.75
        
        val bundles = List(flatPrice2, flatPrice3, pctOff, dealOfTheDay)

        val cartFut = checkout(cart, bundles)
        // Expect minCart total from checkout
        whenReady(cartFut) { cart =>
            assert(cart.total == expectedMinCartTotal)
        }
        
        // All bundle permutations. total of each perm is >= expected minCart total.
        val result = (bundles.permutations).toList.forall { bundles =>
            val cartPerm = applyBundles(cart, bundles)
            cartPerm.total >= expectedMinCartTotal
        }
        
        // Expect minCart total
        assert(result)
    }
    
    /* 
     * To prove greedy application of bundles by the applyBundles method, we 
     * show the first two bundles of each permutation of three are applied (due 
     * to cart item quantities). Because each bundle has a distict bundle price 
     * this means that the resulting list of distinct totals will be 1/2 the 
     * length of the full permutation list.
     */
    "The list size of distinct cart totals" should "be 1/2 the size " +
    "of the full bundle permutations list" in {
        
        val item1 = Item("ItemOne", 1.0)
        val cart = addToCart(item1, 7, getCart)
        
        val flatPrice = bundlePrice(1.99, (item1, 3))()("flatPrice")
        val pctOff = percentPrice(item1, 3, 0.4)()("pctOff")
        val nForM = forPriceOfQty(item1, 3, 1)()("nForM")
        
        val bundlePerm1 = List(flatPrice, pctOff, nForM)
        val bundlePerm2 = List(flatPrice, nForM, pctOff)
        val bundlePerm3 = List(pctOff, nForM, flatPrice)
        val bundlePerm4 = List(pctOff, flatPrice, nForM)
        val bundlePerm5 = List(nForM, flatPrice, pctOff)
        val bundlePerm6 = List(nForM, pctOff, flatPrice)
        
        val bundlePerms = List(bundlePerm1, bundlePerm2, bundlePerm3,
                               bundlePerm4, bundlePerm5, bundlePerm6)
                                                              
        val result = for{
            bundles <- bundlePerms
            cartPerm = applyBundles(cart, bundles)
        } yield cartPerm.total
        
        println(result)
        
        assert(result.length / 2 == result.distinct.length)
    }
    
    /* 
     * This is basically a duplicate of the previous test, but here the
     * permutations are rendered by the List.permutations method, which
     * is used in the app checkout method.
     */
    "Once again, the list size of distinct cart totals" should "be 1/2 the size " +
    "of the full bundle permutations list" in {
        
        val item1 = Item("ItemOne", 1.0)
        val cart = addToCart(item1, 7, getCart)
        
        val flatPrice = bundlePrice(1.99, (item1, 3))()("flatPrice")
        val pctOff = percentPrice(item1, 3, 0.4)()("pctOff")
        val nForM = forPriceOfQty(item1, 3, 1)()("nForM")
        
        val bundlePerms = List(flatPrice, pctOff, nForM).permutations.toList
                                                              
        val result = for{
            bundles <- bundlePerms
            cartPerm = applyBundles(cart, bundles)
        } yield cartPerm.total
        
        println(result)
        
        assert(result.length / 2 == result.distinct.length)
    }
}




