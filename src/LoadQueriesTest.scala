import scala.io.Source

object LoadQueriesTest extends App {

  val queries: Map[Int, String] =
    Source.fromFile("queries.txt").getLines().map(l => l.split(":")).map(e => e(0).toInt -> e(1) ).toMap

  println(queries)
}


