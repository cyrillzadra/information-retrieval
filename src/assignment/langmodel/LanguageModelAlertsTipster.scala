package assignment.langmodel

import assignment.FreqIndex
import assignment.TipsterDirStream
import assignment.io.ResultWriter
import ch.ethz.dal.tinyir.util.StopWatch

class LanguageModelAlertsTipster(queries: Map[Int, String], numberOfResults: Int, tipster: TipsterDirStream, lambda: Double) {

  val idx: LangModelIndex = {
    println("init index")
    println(queries)
    val index = new LangModelIndex(tipster.stream, queries)
    println(queries)

    println("initialized index")
    index
  }

  val alerts = queries.map(x => new LanguageModelAlerts(x._1, x._2, numberOfResults, lambda, idx)).toList

  def process(): Unit = {

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
    new ResultWriter("ranking-l-cyrill-zadra.run").write(this)
  }

  private def process(title: String, doc: List[String]): List[Boolean] = {
    for (alert <- alerts) yield alert.process(title, doc)
  }

  def results = alerts.map(x => x.results)

}