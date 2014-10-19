package assignment.langmodel

import ch.ethz.dal.tinyir.util.StopWatch
import assignment.FreqIndex
import assignment.TipsterDirStream
import assignment.TopicModelAlerts
import assignment.io.ResultWriter

class LanguageModelAlertsTipster(queries: Map[Int, String], numberOfResults: Int, lambda: Double) {

  val alerts = queries.map(x => new TopicModelAlerts(x._1, x._2, numberOfResults, lambda)).toList

  def process(tipster: TipsterDirStream): Unit = {
    //var docIndex = new FreqIndex(tipster.stream)
    var docIndex = null;

    val sw = new StopWatch; sw.start
    var iter = 0
    for (doc <- tipster.stream) {
      iter += 1
      process(doc.name, doc.tokens, docIndex)
      if (iter % 20000 == 0) {

        println("Iteration = " + iter)
        results.foreach(println)
      }
    }
    sw.stop
    println("Stopped time = " + sw.stopped)

    /* output result */
    new ResultWriter("ranking-l-cyrill-zadra.run").write(this)
  }

  private def process(title: String, doc: List[String], docIndex: FreqIndex): List[Boolean] = {
    for (alert <- alerts) yield alert.process(title, doc, docIndex)
  }

  def results = alerts.map(x => x.results)

}