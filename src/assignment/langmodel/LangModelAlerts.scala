package assignment.langmodel

import ch.ethz.dal.tinyir.alerts.Alerts
import ch.ethz.dal.tinyir.alerts.ScoredResult

/**
 * FIXME Refactor need topic as public member. change name from topicConstructor to topic.
 */
class LangModelAlerts(topicConstructur: Int, q: String, numberOfResults: Int, lambda: Double, index: LangModelIndex)
  extends Alerts(q, numberOfResults) {

  val topic = topicConstructur: Int;

  override val query = new LangModelQuery(q, lambda)

  // score a document and try to add to results
  override def process(title: String, doc: List[String]): Boolean = {
    val score = query.score(doc)
    add(ScoredResult(title, score))
  }

}

