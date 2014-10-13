package assignment

import ch.ethz.dal.tinyir.util.StopWatch
import assignment.tdidf.TdIdfAlerts
import ch.ethz.dal.tinyir.processing.Document

class TermBasedModelAlertsTipster(queries: Map[Int, String], numberOfResults: Int, docs: Stream[Document]) {

  val alerts = queries.map(x => new TdIdfAlerts(x._1, x._2, numberOfResults, docs)).toList

  def process(tipster : TipsterDirStream) : Unit = {
    val sw = new StopWatch; sw.start
    var iter = 0
    for (doc <- tipster.stream) {
      iter += 1
      process(doc.name, doc.tokens)
      if (iter % 20000 == 0) {
        println("Iteration = " + iter)
        results.foreach(println)
      }
    }
    sw.stop
    println("Stopped time = " + sw.stopped)
  }

  private def process(title: String, doc: List[String]): List[Boolean] = {
    for (alert <- alerts) yield alert.process(title, doc)
  }

  def results = alerts.map(x => x.results)

}