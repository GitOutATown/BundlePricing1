package bundle_pricing.lab2

object lab1 {

  val chars = List('a', 'b', 'c', 'd', 'e')       //> chars  : List[Char] = List(a, b, c, d, e)

  /*def combine(in: List[Char]): Seq[String] =
    for {
        len <- 1 to in.length
        combinations <- in combinations len
    } yield combinations.mkString
    
  combine(chars)*/
  
  math.pow(2, 5)                                  //> res0: Double = 32.0
  
  val perms = chars.permutations                  //> perms  : Iterator[List[Char]] = non-empty iterator
  perms.length                                    //> res1: Int = 120
  perms foreach println

  '''                                             //> res2: Char('\'') = '
}