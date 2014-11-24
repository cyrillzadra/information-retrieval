package assignment2.svm

import scala.util.control.Breaks._
import breeze.linalg.DenseVector
import breeze.linalg.Vector
import ch.ethz.dal.classifier.processing.ReutersCorpusIterator
import breeze.linalg.DenseMatrix
import breeze.linalg.SparseVector
import assignment2.StopWords
import ch.ethz.dal.classifier.processing.Tokenizer
import com.github.aztek.porterstemmer.PorterStemmer
import assignment2.index.FeatureBuilder
import assignment2.score.PrecisionRecallF1
import assignment2.io.ResultWriter
import ch.ethz.dal.tinyir.util.StopWatch

case class DataPoint(x: SparseVector[Double], y: Double)

case class Postings(val docId: String, val tf: Int)

object SvmClassification extends App {

  val trainDataPath = "C:/dev/projects/eth/information-retrieval/course-material/assignment2/training/train/";
  val testDataLabeledPath = "C:/dev/projects/eth/information-retrieval/course-material/assignment2/test-with-labels/test-with-labels/";

  val trainDataIter: ReutersCorpusIterator = new ReutersCorpusIterator(trainDataPath)
  val testDataLabeledIter: ReutersCorpusIterator = new ReutersCorpusIterator(testDataLabeledPath)

  val featureBuilder: FeatureBuilder = new FeatureBuilder(trainDataIter, testDataLabeledIter)

  val dim: Int = featureBuilder.dim;

  println(featureBuilder.features.take(10))

  //learn
  var topicThetas = scala.collection.mutable.Map[String, SparseVector[Double]]()
  topicThetas ++= featureBuilder.labelCounts.keys.map(x => x -> SparseVector.zeros[Double](dim))
  val sw = new StopWatch; sw.start

  for (theta <- topicThetas) {
    val topic = theta._1
    val samples = 10000;
    var step: Int = 1

    breakable {
      for (featureKey <- featureBuilder.trainDocLabels.keySet) {
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
        if (step == samples) break

      }
    }
    
    println(" time = " + sw.uptonow)


  }

  val resultScore = scala.collection.mutable.Map[String, PrecisionRecallF1[String]]()

  for (doc <- featureBuilder.testDocLabels) {
    val feature = featureBuilder.features(doc._1)
    var scores = scala.collection.mutable.MutableList[(String, Double, Double)]()

    for (theta <- topicThetas) {
      scores ++= List(hingeLoss(theta._1, feature, theta._2))
    }

    val sortedResult = priority(scores.toList);
    //println("score " + doc._1 + " -> " + sortedResult);
    resultScore += doc._1 -> new PrecisionRecallF1(sortedResult, doc._2.toSet)
  }

  new ResultWriter("classify-cyrill-zadra-l-svm.run", resultScore.toMap, true).write()

  def priority(score: List[(String, Double, Double)]): Seq[String] = {
    //println(score)
    score.filter(p => p._2 < p._3).sortBy(_._3).map(s => s._1).toSeq.take(5)
    //score.sortBy(_._3).reverse.map(s => s._1).toSeq.take(5)
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