package assignment.tdidf

import ch.ethz.dal.tinyir.processing.Tokenizer
import ch.ethz.dal.tinyir.alerts.Query
import ch.ethz.dal.tinyir.processing.Document


/**
 * tf * idf : Term Frequency (TF) * Inverse Document Frequency (IDF)
 * 
 * 
 */
class TdIdfQuery(query: String, docs: Stream[Document]) extends Query(query) {

  /** wird nur beim ersten aufrufen initialisiert */
  //lazy val numDocs: Int = docs.size 
  
  override def score(doc: List[String]): Double = {
    val tfs: Map[String, Int] = doc.groupBy(identity).mapValues(l => l.length)
    val qtfs = qterms.flatMap(q => tfs.get(q))
    val numTermsInCommon = qtfs.length
    val docLen = tfs.values.map(x => x * x).sum.toDouble // Euclidian norm
    val queryLen = qterms.length.toDouble
    val termOverlap = qtfs.sum.toDouble / (docLen * queryLen)

    numTermsInCommon + termOverlap
  }
  
//  private def idf( ) : Double = {
//   scala.math.log(numDocs / (0 + 0.5)) 
//  }
}