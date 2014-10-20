package assignment.tdidf

import ch.ethz.dal.tinyir.alerts.Alerts
import ch.ethz.dal.tinyir.alerts.ScoredResult

/**
 * FIXME Refactor need topic as public member. change name from topicConstructor to topic.
 */
class TdIdfAlerts(topicConstructur: Int, qry: String, numberOfResults: Int, index : TdIdfIndex)
  extends Alerts(qry, numberOfResults) {

  val topic = topicConstructur: Int;

  override val query = new TdIdfQuery(qry, index)

  // score a document and try to add to results
  def process(title: String, doc: List[String], index: TdIdfIndex): Boolean = {
    val score = query.score(doc)
    add(ScoredResult(title, score))
  }

}

