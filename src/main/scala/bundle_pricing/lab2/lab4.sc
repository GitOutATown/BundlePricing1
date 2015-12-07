package bundle_pricing.lab2

object lab4 {
  val chars = List('a', 'b', 'c', 'b', 'd', 'e', 'b')
                                                  //> chars  : List[Char] = List(a, b, c, b, d, e, b)
  val target = 'b'                                //> target  : Char = b
  val count = 0                                   //> count  : Int = 0
  val limit = 3                                   //> limit  : Int = 3
  val acc = List[Char]()                          //> acc  : List[Char] = List()
  val context = (acc, count, limit, target)       //> context  : (List[Char], Int, Int, Char) = (List(),0,3,b)
  
  val result = chars.foldRight(context)((c, context) => {
    println("c:" + c + " acc:" + context._1 + " count:" + context._2 + " limit:" + context._3)
    // if (count <= limit)
    if(c == target && context._2 < context._3) (context._1, context._2 + 1, context._3, context._4)
    else (c :: context._1, context._2, context._3, context._4)
  })                                              //> c:b acc:List() count:0 limit:3
                                                  //| c:e acc:List() count:1 limit:3
                                                  //| c:d acc:List(e) count:1 limit:3
                                                  //| c:b acc:List(d, e) count:1 limit:3
                                                  //| c:c acc:List(d, e) count:2 limit:3
                                                  //| c:b acc:List(c, d, e) count:2 limit:3
                                                  //| c:a acc:List(c, d, e) count:3 limit:3
                                                  //| result  : (List[Char], Int, Int, Char) = (List(a, c, d, e),3,3,b)
  result._2 == result._3                          //> res0: Boolean = true
  
  val allOrNone =
    if(result._2 == result._3) result._1
    else chars                                    //> allOrNone  : List[Char] = List(a, c, d, e)
  
  // List('a', 'b', 'c', 'b', 'd', 'e', 'b')

  '''                                             //> res1: Char('\'') = '
}