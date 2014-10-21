package assignment.langmodel

import com.github.aztek.porterstemmer.PorterStemmer

import assignment.util.TestDocument
import ch.ethz.dal.tinyir.processing.Document
import ch.ethz.dal.tinyir.processing.Tokenizer

class LangModelIndex(docsStream: Stream[Document], queries: Map[Int, String]) {

  //FIXME concatenate string in map and remove duplicates
  private val qry: List[String] = Tokenizer.tokenize(PorterStemmer.stem(queries.values.toList.groupBy { _.hashCode() }
    .map { _._2.head }.mkString(" ")))

  private val idx: (collection.mutable.Map[String, Int], Int) = {
    val df = collection.mutable.Map[String, Int]() ++= qry.map(t => t -> 0)
    var nrOfTokens: Int = 0;
    for (doc <- docsStream) {
      df ++= doc.tokens.distinct.filter(t => qry.contains(t)).map(t => t -> (1 + df.getOrElse(t, 0)))
      nrOfTokens += doc.tokens.size
    }
    (df, nrOfTokens)
  }

  private val numberOfTokensInCollection: Double = idx._2
  
  val tokenFrequencies : Map[String, Double] = idx._1.map(kv => (kv._1, kv._2)).toMap.mapValues(x => x / numberOfTokensInCollection)

}

object LangModelIndex {
  def main(args: Array[String]) = {
    val d1 = new TestDocument("1", "mr sherlock holmes who was usually very late")
    val d0 = new TestDocument("0", "i can tell a moriaty when i see one said holmes")
    val d3 = new TestDocument("0", "i can telling a moriaty when i see one said")
    val stream: Stream[TestDocument] = List(d3, d1, d0).toStream

    val query: Map[Int, String] = Map(51 -> "holmes when", 52 -> "holmes test");

    val idx = new LangModelIndex(stream, query)
    println(idx.qry)    
    println(idx.numberOfTokensInCollection)
    println(idx.idx._1)    
    println(idx.tokenFrequencies )
  }

}
 