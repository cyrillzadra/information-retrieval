
case class Result(val category: String, val tf: Int) {
  override def equals(obj: Any): Boolean = obj match {
    case myCase: Result => category.equals(myCase.category)
    case _ => false
  }
}
object Test extends App {

	val l1 = List("c","a","x","c")
	
	println( l1.groupBy(identity)) 
	
	val l2 = List(Result("a",1),Result("c",1),Result("a",1),Result("x",1))
	
	println( l2.groupBy(identity))
	
	println ( l2.groupBy(identity).mapValues(x => x.reduce( (a, b) => Result(a.category, a.tf + b.tf ) ) ) )
	
}