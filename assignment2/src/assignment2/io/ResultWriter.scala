package assignment.io

import java.io.File
import java.io.PrintWriter

import assignment2.score.PrecisionRecallF1

/**
 * Writes result to a given file.
 */
class ResultWriter(fileName: String, result: scala.collection.mutable.Map[String, PrecisionRecallF1[String]]) {

  //classify-[firstname]-[lastname]-[lju]-[nbjlrjsvm].run
  
  val writer = new PrintWriter(new File(fileName))

  //precision recall f1score
  //doc_id topic1 topic2 ...
  def write() = {

    result.foreach { f =>
      writer.write("%s %s %s \n".format(f._2.prF1.precision, f._2.prF1.recall, f._2.prF1.f1))
      writer.write("%s %s \n".format(f._1, f._2.ranked.mkString(" ")))
    }

    writer.close()
  }

}