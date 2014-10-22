package assignment.tdidf

import ch.ethz.dal.tinyir.processing.Tokenizer
import ch.ethz.dal.tinyir.alerts.Query
import ch.ethz.dal.tinyir.processing.Document
import assignment.util.TestDocument

/**
 * tf * idf : Term Frequency * Inverse Document Frequency
 */
class TdIdfQuery(query: String, index: TdIdfIndex) extends Query(query) {

  /**
   * term frequencies
   */
  def tf(doc: List[String]): Map[String, Double] =
    doc.groupBy(identity).mapValues(l => l.length.toDouble)

  /**
   * apply monotonic, sub-linear transformation
   */
  def logtf(tf: Map[String, Double]): Map[String, Double] =
    tf.mapValues(v => math.log(v) + 1.0)

  override def score(doc: List[String]): Double = {

    val ltf: Map[String, Double] = logtf(tf(doc))
    val df: Map[String, Double] = index.idf;

    val filteredQterms = qterms.filter(t => df.contains(t))

    val tfidf = filteredQterms.map(f => ltf.getOrElse(f, 0.0) * index.idf(f))
    tfidf.sum
  }

}

object TdIdfQuery {
  def main(args: Array[String]) = {

    val d1 = new TestDocument("1", "mr sherlock holmes who was usually very late")
    val d0 = new TestDocument("0", "i can tell a moriaty when i see one said holmes")
    val d3 = new TestDocument("3", "i can telling a moriaty when i see one said")
    val stream: Stream[TestDocument] = List(d3, d1, d0).toStream

    val query: Map[Int, String] = Map(51 -> "holmes when", 52 -> "holmes test");
//    val idx = new TdIdfIndex(stream, query)
//
//    val tfidf: TdIdfQuery = new TdIdfQuery("holmes when", idx)
//
//    println(tfidf.score(d1.tokens))

  }
}