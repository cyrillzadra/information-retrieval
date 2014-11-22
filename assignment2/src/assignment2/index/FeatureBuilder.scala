package assignment2.index

import breeze.linalg.SparseVector
import ch.ethz.dal.classifier.processing.ReutersCorpusIterator

class FeatureBuilder(data: ReutersCorpusIterator) {

  val idx = {
    val docs = scala.collection.mutable.Map[String, List[(String, Int)]]()
    val docLength = scala.collection.mutable.Map[String, Int]()
    val labels = scala.collection.mutable.Map[String, List[String]]()
    val labelDocs = scala.collection.mutable.Map[String, List[String]]()
    val labelCounts = scala.collection.mutable.Map[String, Int]()
    val words = scala.collection.mutable.Set[String]()
    while (data.hasNext) {
      val doc = data.next
      val tf = doc.tokens.groupBy(identity).mapValues(l => l.length)
      docs += (doc.name -> tf.toList)
      docLength += (doc.name -> doc.tokens.size)
      words ++= tf.map(c => c._1)
      labels += (doc.name -> doc.topics.toList)
      labelDocs ++= doc.topics.map(c => (c -> (List(doc.name) ++ labelDocs.getOrElse(c, List()))))
      labelCounts ++= doc.topics.map(c => (c -> (1 + labelCounts.getOrElse(c, 0))))
    }

    println("docs size = " + docs.size + " ::: " + docs.take(10))
    println("words size = " + words.size + " ::: " + words.take(10))
    println("topic size = " + labels.size + " ::: " + labels.take(10))
    println("labelDocs size = " + labelDocs.size + " ::: " + labelDocs.take(10))

    val dim = words.size
    val dim_y = docs.size
    
    val wordSeq = words.toSeq
    val wordIndex = words.zipWithIndex.toMap

    //build feature vector
    val features = scala.collection.mutable.Map[String, SparseVector[Double]]()
    for (d <- docs) {
      val v = SparseVector.zeros[Double](dim)
      d._2.map(word => v(wordIndex(word._1)) = word._2)
      features += d._1 -> v
    }
    (features, wordIndex, labels, labelCounts, labelDocs, docLength, wordSeq)
  }

  val features : Map[String, SparseVector[Double]] = idx._1.toMap;

  val words = idx._2;
  
  val wordIndex = idx._7

  val labels = idx._3;
  
  val labelCounts = idx._4;
  
  val labelDocs = idx._5;
  
  val docLength = idx._6;

}

object FeatureBuilder {

  def main(args: Array[String]) = {

    val testIterator = new ReutersCorpusIterator("test/test.zip")
    val f: FeatureBuilder = new FeatureBuilder(testIterator)

    println("features= " + f.features)
    println("words= " + f.words)
    println("wordIndex= " + f.wordIndex)
    println("labels= " + f.labels)
    println("lableCounts= " + f.labelCounts)
    println("labelDocs= " + f.labelDocs)
    println("docLength= " + f.docLength)
    
  }

}
