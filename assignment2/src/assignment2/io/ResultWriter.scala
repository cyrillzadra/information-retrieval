package assignment2.io

import java.io.File
import java.io.PrintWriter

import assignment2.score.PrecisionRecallF1

/**
 * Writes result to a given file.
 */
class ResultWriter(result: Map[String, PrecisionRecallF1[String]], classType: String, labeled: Boolean = true) {

  val fileName = "classify-cyrill-zadra"

  val l: String = if (labeled) "l" else "u"

  /**
   * Filname pattern.
   *
   * classify-[firstname]-[lastname]-[l|u]-[nb|lr|svm].run
   */
  val writer = new PrintWriter(new File(fileName + "-" + l + "-" + classType + ".run"))

  /**
   * output for labeled data:
   *
   * precision recall f1score
   * doc_id topic1 topic2 ...
   *
   * output for unlabeled data:
   * doc_id topic1 topic2 ...
   *
   */
  def write() = {

    var totalF1 = 0.0;
    var totalP = 0.0;
    var totalR = 0.0;
    result.foreach { f =>
      if (labeled && !f._2.prF1.f1.isNaN) {
        totalF1 += f._2.prF1.f1
        totalR += f._2.prF1.recall
        totalP += f._2.prF1.precision

        writer.write("%s %s %s \n".format(f._2.prF1.precision, f._2.prF1.recall, f._2.prF1.f1))
      }
      if (!labeled || !f._2.prF1.f1.isNaN) {
        writer.write("%s %s \n".format(f._1, f._2.ranked.mkString(" ")))
      }
    }

    if (labeled) {
      println("Average::")
      val out = "P= %s , R= %s , F1= %s".format((totalP / result.size.toDouble), (totalR / result.size.toDouble), (totalF1 / result.size.toDouble))
      println(out)
    }

    writer.close()
  }

}