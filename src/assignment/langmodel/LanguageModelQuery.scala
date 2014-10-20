package assignment.langmodel

import ch.ethz.dal.tinyir.processing.Tokenizer
import assignment.FreqIndex
import ch.ethz.dal.tinyir.alerts.Query
import assignment.tdidf.TdIdfQuery
import assignment.tdidf.TdIdfIndex
import assignment.TestDocument
import com.github.aztek.porterstemmer.PorterStemmer

/*
 * lambda value 0.1 for title queries and 0.7 for long queries.
 */
class LanguageModelQuery(query: String, lambda: Double) extends Query(query) {

  def score(doc: List[String], index: LangModelIndex): Double = {
    var numberOfTermsInDocument = doc.size

    val tfs: Map[String, Int] = doc.map(word => PorterStemmer.stem(word)).groupBy(identity).mapValues(l => l.length)

    val qtfs = qterms.flatMap(q => tfs.get(q)).isEmpty match {
      case true => List(0)
      case _ => qterms.flatMap(q => tfs.get(q))
    }

    println(qterms)
    val pPqMd: Double = qtfs.map(x => x.toDouble / numberOfTermsInDocument).reduce(_ * _)
    val pPqMc = qterms.map(x => index.tokenFrequencies(x)).reduce(_ * _)

    ((1 - lambda) * pPqMc) + (lambda * pPqMd)
  }
}

object LanguageModelQuery {
  def main(args: Array[String]) = {

    val d1 = new TestDocument("1", "mr sherlock holmes who was usually very late")
    val d0 = new TestDocument("0", "i can tell a moriaty when i see one said holmes")
    val d3 = new TestDocument("3", "i can telling a moriaty when i see one said")
    val stream: Stream[TestDocument] = List(d3, d1, d0).toStream

    val query: Map[Int, String] = Map(51 -> "holmes when", 52 -> "holmes test");
    val idx = new LangModelIndex(stream, query)

    println(idx.numberOfTokensInCollection)

    println(idx.tokenFrequencies)

    val tfidf: LanguageModelQuery = new LanguageModelQuery("holmes when", 0.1)

    println(tfidf.score(d1.tokens, idx))

  }
}