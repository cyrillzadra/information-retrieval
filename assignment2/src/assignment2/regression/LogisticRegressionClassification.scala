package assignment2.regression

import scala.annotation.migration
import scala.util.Random
import scala.util.control.Breaks.break
import scala.util.control.Breaks.breakable

import assignment2.Classification
import assignment2.index.FeatureBuilder
import assignment2.io.ResultWriter
import assignment2.score.PrecisionRecallF1
import breeze.linalg.SparseVector
import ch.ethz.dal.classifier.processing.ReutersCorpusIterator
import ch.ethz.dal.tinyir.util.StopWatch

class LogisticRegressionClassification(trainDataPath: String, testDataLabeledPath: String, labeled: Boolean) 
  extends Classification {

  val trainDataIter: ReutersCorpusIterator = new ReutersCorpusIterator(trainDataPath)
  val testDataLabeledIter: ReutersCorpusIterator = new ReutersCorpusIterator(testDataLabeledPath)

  val featureBuilder: FeatureBuilder = new FeatureBuilder(trainDataIter, testDataLabeledIter)

  val dim: Int = featureBuilder.dim;

  def process() = {
    println("Start learning")
    //learn
    val rand = new Random()
    var topicThetas = scala.collection.mutable.Map[String, SparseVector[Double]]()
    topicThetas ++= featureBuilder.labelCounts.keys.map(x =>
      x -> SparseVector.fill(dim)(2 * rand.nextDouble - 1))

    val sw = new StopWatch; sw.start

    for (theta <- topicThetas) {
      val topic = theta._1
      val samples = 1;
      var step: Int = 1

      breakable {
        for (featureKey <- featureBuilder.trainDocLabels.keySet) {
          val _t = topicThetas(theta._1)

          val topics = featureBuilder.trainDocLabels(featureKey)
          val y = topics.find(x => x == topic) match {
            case Some(_) => true
            case None    => false
          }

          val feature = featureBuilder.features(featureKey)
          val lambda: Double = 1.0

          //        val gradient = points.map { p =>       p.x * 
          //          (logistic(p.y * w.dot(p.x)) - 1) * p.y }.reduce(_ + _)
    
          val t = update(_t, feature, y)
          topicThetas(theta._1) = t;
          step += 1
          if (step == samples) break

        }
      }

      println("topic =  + " + theta._1 + " time = " + sw.uptonow)

    }

    var resultScore = scala.collection.mutable.Map[String, PrecisionRecallF1[String]]()
    for (doc <- featureBuilder.testDocLabels) {
      val feature = featureBuilder.features(doc._1)
      var scores = scala.collection.mutable.MutableList[(String, Double, Double)]()

      for (theta <- topicThetas) {
        //scores ++= List(logistic(theta._1, feature, theta._2))
      }

      val sortedResult = priority(scores.toList);
      resultScore += doc._1 -> new PrecisionRecallF1(sortedResult, doc._2.toSet)
    }

    new ResultWriter(resultScore.toMap, "lr", labeled).write()

    println("FINISHED")
  }

  def priority(score: List[(String, Double, Double)]): Seq[String] = {
    score.filter(p => p._2 < p._3).sortBy(_._3).map(s => s._1).toSeq.take(5)
  }

  def logistic(x: SparseVector[Double], y: SparseVector[Double]): Double = {
    val r = 1.0 / (1.0 + Math.exp(-x.dot(y)))
    r
  }

  def update(th: SparseVector[Double], x: SparseVector[Double], c: Boolean) = {
    val z = if (c) (1 - logistic(th, x)) else (-logistic(th, x))
    val r = x * z
    r
  }

}

object LogisticRegressionClassification {

  def main(args: Array[String]) = {
    
    val trainDataPath = "C:/dev/projects/eth/information-retrieval/course-material/assignment2/training/train-small/";
    val testDataLabeledPath = "C:/dev/projects/eth/information-retrieval/course-material/assignment2/test-with-labels/test-with-labels/";

    val c = new LogisticRegressionClassification(trainDataPath, testDataLabeledPath, true)

    c.process()
  }

}