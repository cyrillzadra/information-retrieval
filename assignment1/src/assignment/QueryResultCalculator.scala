package assignment

import ch.ethz.dal.tinyir.lectures.PrecisionRecall

class QueryResultCalculator[A](ra: Seq[A], relevance: Set[A], k: Int, t: Int, q: String) {
  
  val topic : Int = t
  
  val query : String = q

  val precisionRecall = new PrecisionRecall(ra, relevance)

  val avgPrecision = new AveragePrecision(ra, relevance, k)

}