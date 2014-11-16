package assignment.io

import java.io.File
import java.io.PrintWriter
import assignment.langmodel.LangModelAlertsTipster
import assignment.tdidf.TdIdfAlertsTipster
import assignment.tdidf.TdIdfAlertsTipster

/**
 * Writes result to a given file.
 */
class ResultWriter(fileName: String) {

  val writer = new PrintWriter(new File(fileName))

  def write(a: TdIdfAlertsTipster) = {
    for (alert <- a.alerts.sortBy(_.topic ); res <- alert.results.zipWithIndex) {
      //val docId = res._1.title.substring(0, res._1.title.length() - 4) + "-" + res._1.title.substring(res._1.title.length() - 4, res._1.title.length())
      val out = "%s %s %s \n".format(alert.topic, res._2 + 1, res._1.title)
      writer.write(out)
    }
    writer.close()
  }
  
    def write(a: LangModelAlertsTipster) = {
    for (alert <- a.alerts.sortBy(_.topic ); res <- alert.results.zipWithIndex) {
      //val docId = res._1.title.substring(0, res._1.title.length() - 4) + "-" + res._1.title.substring(res._1.title.length() - 4, res._1.title.length())
      val out = "%s %s %s \n".format(alert.topic, res._2 + 1, res._1.title)
      writer.write(out)
    }
    writer.close()
  }

}