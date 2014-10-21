package assignment.tdidf

import assignment.AbstractTipster
import assignment.TipsterDirStream
import assignment.io.ResultWriter
import ch.ethz.dal.tinyir.util.StopWatch
import ch.ethz.dal.tinyir.alerts.ScoredResult

class TdIdfAlertsTipster(queries: Map[Int, String], numberOfResults: Int, tipster: TipsterDirStream)
  extends AbstractTipster {

  val idx: TdIdfIndex = {
    println("Starting TfIdf Model")
    new TdIdfIndex(tipster.stream, queries)
  }

  override val alerts : List[TdIdfAlerts] = queries.map(x => new TdIdfAlerts(x._1, x._2, numberOfResults, idx)).toList

  override def process(): Unit = {
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

    /* output result */
    new ResultWriter("ranking-t-cyrill-zadra.run").write(this)
  }

  private def process(title: String, doc: List[String]): List[Boolean] = {
    for (alert <- alerts) yield alert.process(title, doc)
  }

  override def results : List[List[ScoredResult]] = alerts.map(x => x.results)

}