package assignment.tdidf

import assignment.TipsterDirStream
import assignment.io.ResultWriter
import ch.ethz.dal.tinyir.util.StopWatch

/**
 * 
 */
class TermBasedModelAlertsTipster(queries: Map[Int, String], numberOfResults: Int, tipster: TipsterDirStream) {

  val idx: TdIdfIndex = { println("init index")
    val index = new TdIdfIndex(tipster.stream, queries)
    println("initialized index")
    index
  }

  val alerts = queries.map(x => new TdIdfAlerts(x._1, x._2, numberOfResults, idx)).toList

  def process(): Unit = {
    println(queries)
        
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

  def results = alerts.map(x => x.results)

}