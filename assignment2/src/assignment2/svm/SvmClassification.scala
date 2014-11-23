package assignment2.svm

import breeze.linalg.DenseVector
import breeze.linalg.Vector
import ch.ethz.dal.classifier.processing.ReutersCorpusIterator
import breeze.linalg.DenseMatrix
import breeze.linalg.SparseVector
import assignment2.StopWords
import ch.ethz.dal.classifier.processing.Tokenizer
import com.github.aztek.porterstemmer.PorterStemmer
import assignment2.index.FeatureBuilder

case class DataPoint(x: Vector[Double], y: Double)

case class Postings(val docId: String, val tf: Int)

object SvmClassification extends App {

  val trainDataPath = "C:/dev/projects/eth/information-retrieval/course-material/assignment2/training/train-small/";
  val testDataLabeledPath = "C:/dev/projects/eth/information-retrieval/course-material/assignment2/test-with-labels/test-with-labels-small/";

  val trainDataIter: ReutersCorpusIterator = new ReutersCorpusIterator(trainDataPath)
  val testDataLabeledIter: ReutersCorpusIterator = new ReutersCorpusIterator(testDataLabeledPath)

  val featureBuilder: FeatureBuilder = new FeatureBuilder(trainDataIter, testDataLabeledIter)

  val dim: Int = featureBuilder.dim;

  println(featureBuilder.features.take(10))

  //learn
  val yClass = "GCAT"
  var thetaM13 = DenseVector.zeros[Double](dim)

  var step: Int = 1
  for (featureKey <- featureBuilder.trainDocLabels.keySet) {

    val y = featureBuilder.trainDocLabels(featureKey).find(x => x == yClass) match {
      case Some(_) => 1
      case None    => -1
    }

    val feature = featureBuilder.features(featureKey)
    val lambda: Double = 1.0

    thetaM13 = updateStep(thetaM13, new DataPoint(feature, y), lambda, step)
    step += 1
  }

  for (doc <- featureBuilder.testDocLabels) {
    val feature = featureBuilder.features(doc._1)
    val l = hingeLoss(feature)
    val f = if(l._1 < l._2) "NOTFOUND" else "   FOUND"
    println(doc._1 + " : " + f + " -> " + l)
  }

  /**
   *
   *     //Hinge loss l(ThetaVector;(~xVector; y)) = max{0,1 - y<ThetaVector,xVector> }
   */
  def hingeLoss(f: SparseVector[Double]): (Double, Double) = {
    val p = thetaM13.dot(f)
    (math.max(0.0, 1 - (-1 * p)), math.max(0.0, 1 - (1 * p)))
  }

  def updateStep(theta: DenseVector[Double], p: DataPoint,
                 lambda: Double, step: Int) = {
    val thetaShrink = theta * (1 - 1.0 / step.toDouble)
    val margin = 1.0 - (p.y * theta.dot(p.x))
    if (margin <= 0) {
      thetaShrink
    } else
      thetaShrink + (p.x * (1.0 / (lambda * step)) * p.y)
  }

}