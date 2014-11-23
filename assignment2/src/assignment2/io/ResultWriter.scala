package assignment.io

import java.io.File
import java.io.PrintWriter

import assignment2.score.PrecisionRecallF1

/**
 * Writes result to a given file.
 */
class ResultWriter(fileName: String, result: Map[String, PrecisionRecallF1[String]], labeled: Boolean = true) {

  //classify-[firstname]-[lastname]-[lju]-[nbjlrjsvm].run

  val writer = new PrintWriter(new File(fileName))

  //output for labeled data
  //precision recall f1score
  //doc_id topic1 topic2 ...
  
  //output for labeled data
  //doc_id topic1 topic2 ...
  def write() = {

    var totalF1 = 0.0;
    result.foreach { f =>
      if (labeled) {
        if (!f._2.prF1.f1.isNaN) totalF1 += f._2.prF1.f1
        writer.write("%s %s %s \n".format(f._2.prF1.precision, f._2.prF1.recall, f._2.prF1.f1))
      }
      writer.write("%s %s \n".format(f._1, f._2.ranked.mkString(" ")))
    }

    if (labeled) { 
      val out = "F1 Avg = %s".format((totalF1 / result.size.toDouble))
      writer.write(out) 
    }

    writer.close()
  }

}