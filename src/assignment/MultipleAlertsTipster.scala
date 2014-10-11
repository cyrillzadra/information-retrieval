package assignment

class MultipleAlertsTipster(queries: Map[Int ,String], n: Int) {

  val alerts = queries.map(x => new TopicAlerts(x._1, x._2, n)).toList

  def process(title: String, doc: List[String]): List[Boolean] = {
    for (alert <- alerts) yield alert.process(title, doc)
  }
  
  def results = alerts.map(x => x.results)

}