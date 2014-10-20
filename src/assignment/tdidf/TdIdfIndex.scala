package assignment.tdidf

import assignment.TestDocument
import ch.ethz.dal.tinyir.processing.Document
import ch.ethz.dal.tinyir.processing.Tokenizer
import com.github.aztek.porterstemmer.PorterStemmer

class TdIdfIndex(docsStream: Stream[Document], queries: Map[Int, String]) {

  //FIXME concatenate string in map and remove duplicates
  private lazy val qry: List[String] = Tokenizer.tokenize(PorterStemmer.stem(queries.values.toList.groupBy { _.hashCode() }
    .map { _._2.head }.mkString(" ")))

  private lazy val idx: (collection.mutable.Map[String, Int], Int) = {
    val df = collection.mutable.Map[String, Int]() ++= qry.map(t => t -> 0)
    var nr: Int = 0;
    for (doc <- docsStream) {
      df ++= doc.tokens.distinct.filter(t => qry.contains(t)).map(t => t -> (1 + df.getOrElse(t, 0)))
      nr = nr + 1
    }
    (df, nr)
  }

  val numberOfDocuments: Double = idx._2

  val numberOfDocmentsByTerm: Map[String, Int] = idx._1.map(kv => (kv._1, kv._2)).toMap

  val filteredNumberOfDocmentsByTerm: Map[String, Int] = numberOfDocmentsByTerm.filter(x => x._2 != 0)

  val idf: Map[String, Double] = filteredNumberOfDocmentsByTerm.mapValues(x => math.log(numberOfDocuments / x))
  
  //val idf: Map[String, Double] = filteredNumberOfDocmentsByTerm.mapValues(x => log2(numberOfDocuments) - log2(x))
  
  private def log2(x: Double) = math.log10(x) / math.log10(2.0)

}

object TdIdfIndex {
  def main(args: Array[String]) = {
    val d1 = new TestDocument("1", "mr sherlock holmes who was usually very late")
    val d0 = new TestDocument("0", "i can tell a moriaty when i see one said holmes")
    val d3 = new TestDocument("0", "i can telling a moriaty when i see one said")
    val stream: Stream[TestDocument] = List(d3, d1, d0).toStream

    val query: Map[Int, String] = Map(51 -> "holmes when", 52 -> "holmes test");

    val idx = new TdIdfIndex(stream, query)

    println(idx.qry)

    println(idx.numberOfDocmentsByTerm)

    println(idx.numberOfDocuments)

  }

}
 