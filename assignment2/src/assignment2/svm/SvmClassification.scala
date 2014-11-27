package assignment2.svm

import scala.annotation.migration
import scala.util.control.Breaks.break
import scala.util.control.Breaks.breakable
import assignment2.Classification
import assignment2.index.FeatureBuilder
import assignment2.io.ResultWriter
import assignment2.score.PrecisionRecallF1
import breeze.linalg.SparseVector
import ch.ethz.dal.classifier.processing.ReutersCorpusIterator
import ch.ethz.dal.tinyir.util.StopWatch
import scala.util.Random

case class DataPoint(x: SparseVector[Double], y: Double)

class SvmClassification(trainDataPath: String, testDataLabeledPath: String, labeled: Boolean)
  extends Classification {

  val trainDataIter: ReutersCorpusIterator = new ReutersCorpusIterator(trainDataPath)
  val testDataLabeledIter: ReutersCorpusIterator = new ReutersCorpusIterator(testDataLabeledPath)

  val featureBuilder: FeatureBuilder = new FeatureBuilder(trainDataIter, testDataLabeledIter)

  def process() = {
    val dim: Int = featureBuilder.dim;
    val NUMBER_OF_ITERATIONS = 10000;

    println("Start learning")
    val sw = new StopWatch; sw.start
    var topicThetas = scala.collection.mutable.Map[String, SparseVector[Double]]()
    topicThetas ++= featureBuilder.labelCounts.keys.map(x => x -> SparseVector.zeros[Double](dim))

    //pick random train data
    val randomData = Random.shuffle(featureBuilder.trainDocLabels.keySet.toList).take(NUMBER_OF_ITERATIONS)

    for (theta <- topicThetas) {
      val topic = theta._1
      var step: Int = 1

      for (featureKey <- randomData) {
        val _t = topicThetas(theta._1)

        val topics = featureBuilder.trainDocLabels(featureKey)
        val y = topics.find(x => x == topic) match {
          case Some(_) => 1
          case None    => -1
        }

        val feature = featureBuilder.features(featureKey)
        val lambda: Double = 1.0
        val t = updateStep(_t, new DataPoint(feature, y), lambda, step)
        topicThetas(theta._1) = t;
        step += 1

      }   
          println ( sw.uptonow + " s ")

    }
    
    println ("Finished learning " + sw.uptonow + " s ")

    val resultScore = scala.collection.mutable.Map[String, PrecisionRecallF1[String]]()

    for (doc <- featureBuilder.testDocLabels) {
      val feature = featureBuilder.features(doc._1)
      var scores = scala.collection.mutable.MutableList[(String, Double, Double)]()

      for (theta <- topicThetas) {
        scores ++= List(hingeLoss(theta._1, feature, theta._2))
      }

      val sortedResult = priority(scores.toList);
      resultScore += doc._1 -> new PrecisionRecallF1(sortedResult, doc._2.toSet)
    }

    new ResultWriter(resultScore.toMap, "svm", labeled).write()

    println("FINISHED")

  }

  def priority(score: List[(String, Double, Double)]): Seq[String] = {
    score.sortBy(_._3).map(s => s._1).toSeq.take(3)
  }

  /**
   * Hinge loss l(ThetaVector;(~xVector; y)) = max{0,1 - y<ThetaVector,xVector> }
   */
  def hingeLoss(topic: String, f: SparseVector[Double], theta: SparseVector[Double]): (String, Double, Double) = {
    val p = theta.dot(f)
    (topic, math.max(0.0, 1 - (-1 * p)), math.max(0.0, 1 - (1 * p)))
  }

  def updateStep(theta: SparseVector[Double], p: DataPoint,
                 lambda: Double, step: Int) = {
    val stepSize = 1 / (lambda * step)

    val thetaShrink = theta * (1 - (stepSize * lambda))
    val margin = 1.0 - (p.y * theta.dot(p.x))
    if (margin <= 0) {
      thetaShrink
    } else {
      thetaShrink + (p.x * stepSize * p.y)
    }
  }

}

object SvmClassification {

  def main(args: Array[String]) = {

    val trainDataPath = "C:/dev/projects/eth/information-retrieval/course-material/assignment2/training/train/";
    val testDataLabeledPath = "C:/dev/projects/eth/information-retrieval/course-material/assignment2/test-with-labels/test-with-labels/";

    val c = new SvmClassification(trainDataPath, testDataLabeledPath, false)

    c.process()
  }

}