
package assignment2.score

import collection.Seq
import util.Random
import math.{ min, max }

class PrecisionRecallF1[A](_ranked: Seq[A], relev: Set[A]) {

  //precision recall f1 score
  val prF1 = evaluate(_ranked, relev)
  
  //ranked items
  val ranked = _ranked;
  
  // total number of relevant items  
  val num = relev.size

  // indices in result list at which relevant items occur
  val relevIdx = ranked.zipWithIndex.filter { case (r, _) => relev(r) }.map(_._2).toArray

  // precision values at index positions relevIdx
  val precs = relevIdx.zipWithIndex.map { case (rnk, rel) => (rel + 1) / (rnk + 1).toDouble }

  // interpolation of precision to all recall levels 
  val iprecs = precs.scanRight(0.0)((a, b) => Math.max(a, b)).dropRight(1)

  // number of results to reach recall level 
  private def recall2num(recall: Double) = {
    assert(recall >= 0.0 && recall <= 1.0)
    min((recall * num).ceil.toInt, num)
  }

  // precision at recall level 
  def precAt(recall: Double, interpolated: Boolean = false) = {
    assert(recall >= 0.0 && recall <= 1.0)
    val n = max(1, recall2num(recall))
    if (interpolated) iprecs(n - 1)
    else precs(n - 1)
  }

  case class PrecRec(precision: Double, recall: Double, f1: Double) {
    def mkstr: String = "P = " + precision + ", R = " + recall + ", F1 = " + f1
  }

  def evalF1(prec: Double, recall: Double): Double = {
    val beta: Double = 1.0
    ((math.pow(beta, 2) + 1) * prec * recall) / (math.pow(beta, 2) * prec + recall)
  }

  def evaluate[A](ranked: Seq[A], relev: Set[A]) = {
    val truePos = (ranked.toSet & relev).size
    PrecRec(
      precision = truePos.toDouble / ranked.size.toDouble,
      recall = truePos.toDouble / relev.size.toDouble,
      f1 = evalF1(truePos.toDouble / ranked.size.toDouble, truePos.toDouble / relev.size.toDouble))
  }

}

object PrecisionRecallF1 {

  def main(args: Array[String]) = {
    {
      val ranked = Seq(3, 5, 10, 11, 12, 13)
      val relevant = Random.shuffle((0 to 7).toSet)

      val pr = new PrecisionRecallF1(ranked, relevant)
      println(pr.relevIdx.mkString(" "))
      println(pr.precs.mkString(" "))
      println(pr.iprecs.mkString(" "))

      val prF1 = pr.evaluate(ranked, relevant)

      println(prF1.mkstr)

      assert((prF1.precision == (1.0 / 3.0)))
      assert((prF1.recall == (1.0 / 4.0)))
      assert((math.round(prF1.f1 * 100) == math.round((2.0 / 7.0) * 100)))

      val recall = 0.25
      println("Precision (non interp. ) at " + recall + " = " + pr.precAt(recall, false))
      println("Precision (interpolated) at " + recall + " = " + pr.precAt(recall, true))
    }
  }
}