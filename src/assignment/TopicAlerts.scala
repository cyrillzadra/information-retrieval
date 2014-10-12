package assignment

import ch.ethz.dal.tinyir.alerts.Alerts

/**
 * FIXME Refactor need topic as public member. change name from topicConstructor to topic.
 */
class TopicAlerts(topicConstructur: Int, query: String, numberOfResults: Int) extends Alerts(query, numberOfResults) {

  val topic: Int = topicConstructur;

}

