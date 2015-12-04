package bundle_pricing.lab

import Entities._

// Product or Bundle
trait Item {
    def id: String
    def price: Double
}

// Groupings of Products with special price
trait Bundle extends Item {
    def prods: List[Option[Product]]
}

trait CatalogService {
    def getCart(): Cart
    def minCartTotal(): Double // determine cart with min total
    def addProd(prod: Product, cart: Cart): Cart // Bundles are not packaged together
}

object Catalog {
    def addProd(prod: Product, store: Store): Store = {
       store.copy(prods = prod :: store.prods)
    }
    
    def addProds(prods: List[Product], store: Store): Store = {
        store.copy(prods = prods ++ store.prods)
    }
}

object Entities {
    
    case class Store(
        prods: List[Item] = Nil,
        bundles: List[Bundle] = Nil 
    )

    case class Product(
        id: String, 
        price: Double
    ) extends Item
    
    case class Bundle(
        id: String, 
        price: Double, 
        prods: List[Product]
    ) extends Item

    case class Cart(
        prods: List[Product] = Nil,
        bundles: List[Bundle] = Nil,
        total: Double = 0.0
    )
}

object ShoppingTrip extends CatalogService {

    def getCart(): Cart = Cart()
    
    def minCartTotal(): Double = {
        0 // STUB
    }
    
    def addProd(prod: Product, cart: Cart): Cart = {
       cart.copy(prods = prod :: cart.prods)
    }
    def addProds(prods: List[Product], cart: Cart): Cart = {
        cart.copy(prods = prods ++ cart.prods)
    }
}




