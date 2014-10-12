package assignment

import ch.ethz.dal.tinyir.alerts.Alerts

/**
 * FIXME Refactor need topic as public member. change name from topicConstructor to topic.
 */
class TopicModelAlerts(topicConstructur: Int, query: String, numberOfResults: Int, lambda: Double)
  extends ModelAlerts(query, numberOfResults, lambda) {

  val topic: Int = topicConstructur;

}

