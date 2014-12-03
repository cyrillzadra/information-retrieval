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

  def process() = {
    val dim: Int = featureBuilder.dim;
    val NUMBER_OF_ITERATIONS = 10000;

    println("Start learning step")
    val sw = new StopWatch; sw.start

    val rand = new Random()
    var topicThetas = scala.collection.mutable.Map[String, SparseVector[Double]]()
    topicThetas ++= featureBuilder.labelCounts.keys.map(x =>
      x -> SparseVector.zeros[Double](dim))

    //pick random train data
    val randomData = Random.shuffle(featureBuilder.trainDocLabels.keySet.toList).take(NUMBER_OF_ITERATIONS)

    for (theta <- topicThetas) {
      val topic = theta._1
      var step: Int = 1

      for (featureKey <- randomData) {
        val _t = topicThetas(theta._1)

        val topics = featureBuilder.trainDocLabels(featureKey)
        val y = topics.find(x => x == topic) match {
          case Some(_) => true
          case None    => false
        }

        val feature = featureBuilder.features(featureKey)

        val numDocs : Double = featureBuilder.trainDocLabels.size;
        val positiv : Double = featureBuilder.trainLabelDocs(topic).size
        val nevativ : Double = numDocs - positiv

        val alpha: Double = if (y) (nevativ/ numDocs) else (positiv / numDocs)

        val t = update(_t, feature, y, alpha)
        topicThetas(theta._1) = t;
        step += 1

      }
      println(sw.uptonow + " s ")
    }
    
    println ("Finished learning " + sw.uptonow + " s ")
    println("Start classification step")
    
    var resultScore = scala.collection.mutable.Map[String, PrecisionRecallF1[String]]()
    for (doc <- featureBuilder.testDocLabels) {
      val feature = featureBuilder.features(doc._1)
      var scores = scala.collection.mutable.MutableList[(String, Double)]()

      for (theta <- topicThetas) {
        val s = logistic(theta._2, feature)
        scores ++= List((theta._1, s))
      }

      val sortedResult = priority(scores.toList);
      resultScore += doc._1 -> new PrecisionRecallF1(sortedResult, doc._2.toSet)
      
    }

    new ResultWriter(resultScore.toMap, "lr", labeled).write()

    println("FINISHED")
  }

  def priority(score: List[(String, Double)]): Seq[String] = {
    val threshold = 0.6;
    score.filter(s => s._2 > threshold).sortBy(_._2).reverse.map(s => s._1).toSeq
  }

  def logistic(x: SparseVector[Double], y: SparseVector[Double]): Double = {
    val r = 1.0 / (1.0 + Math.exp(-x.dot(y)))
    r
  }
  
  def update(th: SparseVector[Double], x: SparseVector[Double], c: Boolean, alpha: Double) = {
    val z = if (c) alpha * (1 - logistic(th, x)) else -alpha * (-logistic(th, x))
    val r = x * z
    r
  }

}

object LogisticRegressionClassification {

  def main(args: Array[String]) = {

    val trainDataPath = "C:/dev/projects/eth/information-retrieval/course-material/assignment2/training/train-small/";
    val testDataPath = "C:/dev/projects/eth/information-retrieval/course-material/assignment2/test-with-labels/test-with-labels-small/";

    val c = new LogisticRegressionClassification(trainDataPath, testDataPath, true)

    c.process()
  }

}