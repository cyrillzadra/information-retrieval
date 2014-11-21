package assignment2.svm

import breeze.linalg.DenseVector
import breeze.linalg.Vector
import ch.ethz.dal.classifier.processing.ReutersCorpusIterator
import breeze.linalg.DenseMatrix
import breeze.linalg.SparseVector
import assignment2.StopWords
import ch.ethz.dal.classifier.processing.Tokenizer
import com.github.aztek.porterstemmer.PorterStemmer

case class DataPoint(x: Vector[Double], y: Double)

case class Postings(val docId: String, val tf: Int)

object SvmClassification extends App {

  val trainDataPath = "C:/dev/projects/eth/information-retrieval/course-material/assignment2/training/train_small/";
  val trainDataIter: ReutersCorpusIterator = new ReutersCorpusIterator(trainDataPath)

  val docs = scala.collection.mutable.Map[String, List[(String, Int)]]()
  val topics = scala.collection.mutable.Map[String, List[String]]()
  val words = scala.collection.mutable.Set[String]()
  while (trainDataIter.hasNext) {
    val doc = trainDataIter.next

    val tf = doc.tokens.groupBy(identity).mapValues(l => l.length)

    docs += (doc.name -> tf.toList)
    words ++= tf.map(c => c._1)

    topics += (doc.name -> doc.topics.toList)

  }

  println("docs size = " + docs.size + " ::: " + docs.take(10))
  println("words size = " + words.size + " ::: " + words.take(10))
  println("topic size = " + topics.size + " ::: " + topics.take(10))

  val dim = words.size
  val dim_y = docs.size
  val wordIndex = words.zipWithIndex.toMap

  //build feature vector
  val features = scala.collection.mutable.Map[String, SparseVector[Double]]()
  for (d <- docs) {
    val v = SparseVector.zeros[Double](dim)
    d._2.map(word => v(wordIndex(word._1)) = word._2)
    features += d._1 -> v
  }

  println(features.take(10))

  //learn
  val yClass = "M13"
  var thetaM13 = DenseVector.zeros[Double](dim)
  var step: Int = 1
  for (f <- features) {
    val y = topics(f._1).find(x => x == yClass) match {
      case Some(_) => 1
      case None    => -1
    }

    val lambda: Double = 1.0

    thetaM13 = updateStep(thetaM13, new DataPoint(f._2, y), lambda, step)
    step += 1
  }

  //println(thetaM13)

  //predict
  //Hinge loss l(ThetaVector;(~xVector; y)) = max{0,1 - y<ThetaVector,xVector> }

  val t: String = "All but one Helibor interest rates were steady at the Bank of Finland's daily fixing on Friday. The three-month rate was flat at 3.07 percent. February 14 fix  February 13 fix 1-mth Helibor   3.00 pct    3.00 pct 2-mth Helibor    3.04 pct    3.04 pct  3-mth Helibor   3.07 pct    3.07 pct  6-mth Helibor   3.16 pct    3.16 pct  9-mth Helibor   3.25 pct    3.24 pct  12-mth Helibor   3.32 pct   3.32 pct -- Helsinki Newsroom +358-9-680 50 248"
  val tt = StopWords.filterNot(Tokenizer.tokenize(PorterStemmer.stem(t))).toList

  //build test feature vector
  val fV = SparseVector.zeros[Double](dim)
  tt.groupBy(identity).mapValues(l => l.length).map(word => fV(wordIndex(word._1)) = word._2)

  val p = thetaM13.dot(fV)

  println(math.max(0.0, 1 - (-1 * p)))
  println(math.max(0.0, 1 - (1 * p)))

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