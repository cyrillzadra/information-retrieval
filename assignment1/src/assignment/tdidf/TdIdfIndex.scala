package assignment.tdidf

import assignment.util.TestDocument
import ch.ethz.dal.tinyir.processing.Document
import ch.ethz.dal.tinyir.processing.Tokenizer
import com.github.aztek.porterstemmer.PorterStemmer
import assignment.io.MyStream
import ch.ethz.dal.tinyir.util.StopWatch
import assignment.TipsterDirStream

class TdIdfIndex(docsStream: MyStream, queries: Map[Int, String]) {

  //FIXME concatenate string in map and remove duplicates
  private val qry: List[String] = Tokenizer.tokenize(PorterStemmer.stem(queries.values.toList.groupBy { _.hashCode() }
    .map { _._2.head }.mkString(" ")))

  private val idx: (collection.mutable.Map[String, Int], Int) = {
    //document frequencies
    val df = collection.mutable.Map[String, Int]() ++= qry.map(t => t -> 0)
    val sw = new StopWatch; sw.start
    var iter = 0
    var nrOfDocuments: Int = 0;
    for (doc <- docsStream.stream) {
      iter += 1
      if (iter % 20000 == 0) {
        println("Iteration = " + iter + " time = " + sw.uptonow)
      }

      df ++= doc.tokens.distinct.filter(t => qry.contains(t)).map(t => t -> (1 + df.getOrElse(t, 0)))
      nrOfDocuments += +1
    }
    sw.stop
    println("Stopped time = " + sw.stopped)
    (df, nrOfDocuments)
  }

  private val numberOfDocuments: Double = idx._2

  //clone to immutable map
  private val numberOfDocmentsByTerm: Map[String, Int] = idx._1.map(kv => (kv._1, kv._2)).toMap

  //filter all query terms which have zero occurences in whole document collection.
  private val filteredNumberOfDocmentsByTerm: Map[String, Int] = numberOfDocmentsByTerm.filter(x => x._2 != 0)

  val idf: Map[String, Double] = filteredNumberOfDocmentsByTerm.mapValues(x => math.log(numberOfDocuments / x))

}

object TdIdfIndex {
  def main(args: Array[String]) = {
    val d1 = new TestDocument("1", "mr sherlock holmes who was usually very late")
    val d0 = new TestDocument("0", "i can tell a moriaty when i see one said holmes")
    val d3 = new TestDocument("0", "i can telling a moriaty when i see one said")
    val stream: Stream[TestDocument] = List(d3, d1, d0).toStream

    val query: Map[Int, String] = Map(51 -> "holmes when", 52 -> "holmes test");

    //    val idx = new TdIdfIndex(stream, query)

    //    println(idx.qry)
    //
    //    println(idx.numberOfDocmentsByTerm)
    //
    //    println(idx.numberOfDocuments)

  }

}
 