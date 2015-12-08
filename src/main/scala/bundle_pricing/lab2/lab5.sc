package bundle_pricing.lab2

object lab5 {

  case class Bar(y: String)

  case class Foo(x: String)
  val baz = Foo("Baz")                            //> baz  : bundle_pricing.lab2.lab5.Foo = Foo(Baz)
  baz.x                                           //> res0: String = Baz

  val res1 = for{
    foo <- List[Foo]()
  } yield foo.x                                   //> res1  : List[String] = List()
  
  res1                                            //> res1: List[String] = List()

  '''                                             //> res2: Char('\'') = '
}