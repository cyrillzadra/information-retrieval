package assignment.io

import java.io.File
import java.io.PrintWriter

import assignment.tdidf.TermBasedModelAlertsTipster
import assignment.langmodel.LanguageModelAlertsTipster

/**
 * Writes result to a given file.
 */
class ResultWriter(fileName: String) {

  val writer = new PrintWriter(new File(fileName))

  def write(a: TermBasedModelAlertsTipster) = {
    for (alert <- a.alerts; res <- alert.results.zipWithIndex) {
      //val docId = res._1.title.substring(0, res._1.title.length() - 4) + "-" + res._1.title.substring(res._1.title.length() - 4, res._1.title.length())
      val out = "%s %s %s \n".format(alert.topic, res._2 + 1, res._1.title)
      writer.write(out)
    }
    writer.close()
  }
  
    def write(a: LanguageModelAlertsTipster) = {
    for (alert <- a.alerts; res <- alert.results.zipWithIndex) {
      //val docId = res._1.title.substring(0, res._1.title.length() - 4) + "-" + res._1.title.substring(res._1.title.length() - 4, res._1.title.length())
      val out = "%s %s %s \n".format(alert.topic, res._2 + 1, res._1.title)
      writer.write(out)
    }
    writer.close()
  }

}