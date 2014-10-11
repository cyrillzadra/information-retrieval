package assignment

import scala.collection.Seq

/**
 * TODO refactor!!!
 */
class AveragePrecision[A](ra: Seq[A], relevance: Set[A], k: Int) {

  private val ranked: Seq[A] = if (ra.length > k) ra.take(k) else ra

  def avgPrecision(): Float = {
    var numHit = 0
    val precisions =
      ranked.zipWithIndex.map {
        case (prediction, index) =>
          if (relevance.contains(prediction)) {
            numHit += 1
            numHit.toFloat / (index + 1)
          } else {
            0
          }
      }

    if (relevance.size == 0) 0 else (precisions.sum / math.min(relevance.size, k))
  }
}