package assignment.langmodel

import scala.collection.mutable.PriorityQueue
import scala.math.Ordering.Implicits._
import assignment.FreqIndex
import ch.ethz.dal.tinyir.alerts.Alerts
import ch.ethz.dal.tinyir.alerts.ScoredResult

/**
 * FIXME Refactor need topic as public member. change name from topicConstructor to topic.
 */
class LanguageModelAlerts(topicConstructur: Int, q: String, numberOfResults: Int, lambda: Double)
  extends Alerts(q, numberOfResults) {
  
  val topic = topicConstructur: Int;

  override val query = new LanguageModelQuery(q, lambda)

  // score a document and try to add to results
  def process(title: String, doc: List[String], index: LangModelIndex): Boolean = {
    val score = query.score(doc, index)
    add(ScoredResult(title, score))
  }

}

