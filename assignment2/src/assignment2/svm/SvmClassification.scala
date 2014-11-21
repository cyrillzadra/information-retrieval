package assignment2.svm

import breeze.linalg.DenseVector
import breeze.linalg.Vector
import ch.ethz.dal.classifier.processing.ReutersCorpusIterator
import breeze.linalg.DenseMatrix
import breeze.linalg.SparseVector

case class DataPoint(x: Vector[Double], y: Double)

case class Postings(val docId: String, val tf: Int)

object SvmClassification extends App {

  val trainDataPath = "C:/dev/projects/eth/information-retrieval/course-material/assignment2/training/train/";

  val trainDataIter: ReutersCorpusIterator = new ReutersCorpusIterator(trainDataPath)

  val docs = scala.collection.mutable.Map[String, List[(String, Int)]]()
  val topics = scala.collection.mutable.Map[String, List[String]]()
  //val features_words = scala.collection.mutable.Map[String, List[Postings]]()
  val words = scala.collection.mutable.Map[String, Int]()

  while (trainDataIter.hasNext) {
    val doc = trainDataIter.next

    val tf = doc.tokens.groupBy(identity).mapValues(l => l.length)

    docs += (doc.name -> tf.toList)

    //val tf_docId: Map[String, List[Postings]] = tf.map(x => (x._1 -> List(new Postings(doc.name, x._2))))
    //features_words ++= tf_docId.map(c => (c._1 ->
    //  (c._2 ++ features_words.getOrElse[List[Postings]](c._1, List()))))
    words ++= tf.map(c => (c._1 -> (c._2 + words.getOrElse(c._1, 0))))

    topics += (doc.name -> doc.topics.toList)

  }

  println(docs.take(10))
  println(words.take(10))
  println(topics.take(10))

  val dim = words.size
  val dim_y = docs.size
  val wordIndex = words.keys.zipWithIndex.toMap

  //init feature vector
  val m = scala.collection.mutable.Map[String, SparseVector[Int]]()
  for (d <- docs) {
    val v = SparseVector.zeros[Int](dim)
    d._2.map(word => v(wordIndex(word._1)) = word._2)
    m += d._1 -> v
  }
  println(m.take(10))

  
  
  
  def updateStep(theta: DenseVector[Double], p: DataPoint,
                 lambda: Double, step: Int) = {
    val thetaShrink = theta * (1 - 1.0 / step.toDouble)
    val margin = 1.0 - (p.y * theta.dot(p.x))
    if (margin <= 0)
      thetaShrink
    else
      thetaShrink + (p.x * (1.0 / (lambda * step)) * p.y)
  }

}