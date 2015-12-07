package bundle_pricing.lab2

object lab3 {

  val chars = List('a', 'b', 'c', 'b', 'd', 'e', 'b')
                                                  //> chars  : List[Char] = List(a, b, c, b, d, e, b)
  chars.find(_ == 'b')                            //> res0: Option[Char] = Some(b)
  
  chars.filter(_ != 'b')                          //> res1: List[Char] = List(a, c, d, e)
  
  chars.takeWhile(_ != 'b')                       //> res2: List[Char] = List(a)
  
  chars.span {_ == 'b'}                           //> res3: (List[Char], List[Char]) = (List(),List(a, b, c, b, d, e, b))
  
  val z = (List[Char](), 0)                       //> z  : (List[Char], Int) = (List(),0)
  
  val f = (c:Char, acc:(List[Char], Int)) => {
    (c :: acc._1, acc._2 + 1)
  }                                               //> f  : (Char, (List[Char], Int)) => (List[Char], Int) = <function2>
  
  chars.foldRight(z)(f)                           //> res4: (List[Char], Int) = (List(a, b, c, b, d, e, b),7)
  
  chars.foldRight((List[Char](), 0))((c, acc) => {
    (c :: acc._1, acc._2 + 1)
  })                                              //> res5: (List[Char], Int) = (List(a, b, c, b, d, e, b),7)
  
  // -------------------------- //
  
  val target = 'b'                                //> target  : Char = b
  val count = 0                                   //> count  : Int = 0
  val limit = 4                                   //> limit  : Int = 4
  val acc = List[Char]()                          //> acc  : List[Char] = List()
  val context = (acc, count, limit, target)       //> context  : (List[Char], Int, Int, Char) = (List(),0,4,b)
  
  val result = chars.foldRight(context)((c, context) => {
    println("c:" + c + " acc:" + context._1 + " count:" + context._2 + " limit:" + context._3)
    // if (count <= limit)
    if(c == target && context._2 < context._3) (context._1, context._2 + 1, context._3, context._4)
    else (c :: context._1, context._2, context._3, context._4)
  })                                              //> c:b acc:List() count:0 limit:4
                                                  //| c:e acc:List() count:1 limit:4
                                                  //| c:d acc:List(e) count:1 limit:4
                                                  //| c:b acc:List(d, e) count:1 limit:4
                                                  //| c:c acc:List(d, e) count:2 limit:4
                                                  //| c:b acc:List(c, d, e) count:2 limit:4
                                                  //| c:a acc:List(c, d, e) count:3 limit:4
                                                  //| result  : (List[Char], Int, Int, Char) = (List(a, c, d, e),3,4,b)
  
  result._2 >= result._3                          //> res6: Boolean = false
  
  // List('a', 'b', 'c', 'b', 'd', 'e', 'b')

  '''                                             //> res7: Char('\'') = '
}