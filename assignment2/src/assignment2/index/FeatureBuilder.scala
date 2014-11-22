package assignment2.index

import breeze.linalg.SparseVector
import ch.ethz.dal.classifier.processing.ReutersCorpusIterator

class FeatureBuilder(data: ReutersCorpusIterator) {

  val idx = {
    val docs = scala.collection.mutable.Map[String, List[(String, Int)]]()
    val labels = scala.collection.mutable.Map[String, List[String]]()
    val words = scala.collection.mutable.Set[String]()
    while (data.hasNext) {
      val doc = data.next
      val tf = doc.tokens.groupBy(identity).mapValues(l => l.length)
      docs += (doc.name -> tf.toList)
      words ++= tf.map(c => c._1)
      labels += (doc.name -> doc.topics.toList)
    }

    println("docs size = " + docs.size + " ::: " + docs.take(10))
    println("words size = " + words.size + " ::: " + words.take(10))
    println("topic size = " + labels.size + " ::: " + labels.take(10))

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
    (features, wordIndex, labels)
  }
  
  val features = idx._1;
  
  val words = idx._2;
  
  val labels = idx._3;

}