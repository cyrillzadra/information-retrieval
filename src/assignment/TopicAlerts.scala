package assignment

import ch.ethz.dal.tinyir.alerts.Alerts

class TopicAlerts (t: Int , q: String, n: Int) extends Alerts(q,n) {
  
  val topic : Int = t;
  
}

