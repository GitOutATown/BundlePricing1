package bundle_pricing.lab

// Product or Bundle
trait CartItem {
    def id: String
    def price: Double
}

// Groupings of Products with special price
trait CartBundle extends CartItem {
    def prods: List[Option[Product]]
}

trait CartService {
    //def priceGoupings: List[List[CartItem]]
    def getCart(): Cart
    def minCartTotal(): Double // determine cart with min total
    def addProd(prod: Product, cart: Cart): Cart // Bundles are not packaged together
}

object StoreService {
    def addProd(prod: Product, store: Store): Store = {
       store.copy(prods = prod :: store.prods)
    }
    
    def addProds(prods: List[Product], store: Store): Store = {
        store.copy(prods = prods ++ store.prods)
    }
}

case class Store(
    prods: List[Product] = Nil,
    bundles: List[CartBundle] = Nil 
)

case class Product(
    id: String, 
    price: Double
) extends CartItem

case class Bundle(
    id: String, 
    price: Double, 
    prods: List[Product]
) extends CartItem

case class Cart(
    prods: List[Product] = Nil,
    bundles: List[CartBundle] = Nil,
    total: Double = 0.0
)

object Shopping extends CartService {

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




