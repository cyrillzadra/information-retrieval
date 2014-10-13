package assignment.tdidf

import scala.math.Ordering.Implicits._
import ch.ethz.dal.tinyir.alerts.Alerts
import ch.ethz.dal.tinyir.processing.Document

/**
 * FIXME Refactor need topic as public member. change name from topicConstructor to topic.
 */
class TdIdfAlerts(topicConstructur: Int, qry: String, numberOfResults: Int, docs: Stream[Document])
  extends Alerts(qry, numberOfResults) {

  val topic = topicConstructur: Int;

  override val query = new TdIdfQuery(qry, docs)

}

