package assignment

import java.io.File
import java.io.PrintWriter

import ch.ethz.dal.tinyir.alerts.Alerts

/**
 * Writes result to a given
 */
class ResultWriter(fileName: String) {

  val writer = new PrintWriter(new File(fileName))

  def write(a: MultipleAlertsTipster) = {
    for (alert <- a.alerts; res <- alert.results.zipWithIndex) {
      val docId = res._1.title.substring(0, res._1.title.length() - 4) + "-" + res._1.title.substring(res._1.title.length() - 4, res._1.title.length())
      val out = "%s %s %s \n".format(alert.topic, res._2 + 1, docId)
      writer.write(out)
    }
    writer.close()
  }

}