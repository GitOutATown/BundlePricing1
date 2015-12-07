package bundle_pricing.lab2

object lab2 extends App {

    val chars = List('a', 'b', 'c', 'd', 'e')
    
    val perms = chars.permutations
    
    perms foreach println
    
    perms.length
}