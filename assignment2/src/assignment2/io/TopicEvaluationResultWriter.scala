package assignment2.io

import java.io.File
import java.io.PrintWriter

import assignment2.score.PrecisionRecallF1

class TopicEvaluationResultWriter(result: scala.collection.mutable.Map[String, PrecisionRecallF1[String]]) {

  def write() = {

    val writer = new PrintWriter(new File("naive-base-topic-evaluation.csv"))
    writer.write("Topics , Avg F1")
    println("write topic evaluation file")
    for (i <- (1 to 20).reverse) {
      var totalF1 = 0.0;
      var total = 0;
      result.map(f => f._1 -> f._2.evaluatePreRec(i)).foreach { f =>
        if (!f._2.f1.isNaN) {
          println(f._2.f1)
          totalF1 += f._2.f1;
          total += 1;
        }
      }

      val out = "%s , %s".format(i, totalF1 / total)

      writer.write(out) 

    }
    
    writer.close()

  }

}