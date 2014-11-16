package assignment.langmodel

import assignment.AbstractTipster
import assignment.TipsterDirStream
import assignment.io.ResultWriter
import ch.ethz.dal.tinyir.util.StopWatch
import ch.ethz.dal.tinyir.alerts.ScoredResult
import assignment.io.MyStream

class LangModelAlertsTipster(queries: Map[Int, String], numberOfResults: Int, tipster: MyStream, lambda: Double)
  extends AbstractTipster {

  val idx: LangModelIndex = {
    println("Starting Language Model")
    new LangModelIndex(tipster, queries)
  }

  override val alerts = queries.map(x => new LangModelAlerts(x._1, x._2, numberOfResults, lambda, idx)).toList

  override def process(): Unit = {

    val sw = new StopWatch; sw.start
    var iter = 0
    for (doc <- tipster.stream) {
      if (doc.isFile) {
        iter += 1
        process(doc.name, doc.tokens)
        if (iter % 20000 == 0) {

          println("Iteration = " + iter + " time = " + sw.uptonow)
          //          results.foreach(println)
        }
      }
    }
    sw.stop
    println("Stopped time = " + sw.stopped)

    /* output result */
    new ResultWriter("ranking-l-cyrill-zadra.run").write(this)
  }

  private def process(title: String, doc: List[String]): List[Boolean] = {
    for (alert <- alerts) yield alert.process(title, doc)
  }

  override def results: List[List[ScoredResult]] = alerts.map(x => x.results)

}