package assignment2.index

import breeze.linalg.SparseVector
import ch.ethz.dal.classifier.processing.ReutersCorpusIterator

class FeatureBuilder(train: ReutersCorpusIterator, test: ReutersCorpusIterator) {

  val idx = {
    val docs = scala.collection.mutable.Map[String, List[(String, Int)]]()
    val trainDocLength = scala.collection.mutable.Map[String, Int]()
    val trainDocLabels = scala.collection.mutable.Map[String, List[String]]()
    val testDocLabels = scala.collection.mutable.Map[String, List[String]]()
    val trainLabelDocs = scala.collection.mutable.Map[String, List[String]]()
    val labelCounts = scala.collection.mutable.Map[String, Int]()
    val words = scala.collection.mutable.Set[String]()
    while (train.hasNext) {
      val doc = train.next
      val tf = doc.tokens.groupBy(identity).mapValues(l => l.length)
      docs += (doc.name -> tf.toList)
      trainDocLength += (doc.name -> doc.tokens.size)
      words ++= tf.map(c => c._1)
      trainDocLabels += (doc.name -> doc.topics.toList)
      trainLabelDocs ++= doc.topics.map(c => (c -> (List(doc.name) ++ trainLabelDocs.getOrElse(c, List()))))
      labelCounts ++= doc.topics.map(c => (c -> (1 + labelCounts.getOrElse(c, 0))))
    }

    while (test.hasNext) {
      val doc = test.next
      val tf = doc.tokens.groupBy(identity).mapValues(l => l.length)
      docs += (doc.name -> tf.toList)
      words ++= tf.map(c => c._1)
      testDocLabels += (doc.name -> doc.topics.toList)
    }

    println("train docs size = " + docs.size + " ::: " + docs.take(10))
    println("train words size = " + words.size + " ::: " + words.take(10))
    println("train trainDocLabels size = " + trainDocLabels.size + " ::: " + trainDocLabels.take(10))
    println("train trainLabelDocs size = " + trainLabelDocs.size + " ::: " + trainLabelDocs.take(10))

    println("train testDocLabels size = " + testDocLabels.size + " ::: " + testDocLabels.take(10))

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

    (features, wordIndex, trainDocLabels, labelCounts, trainLabelDocs, trainDocLength, wordSeq, testDocLabels)
  }

  val features: Map[String, SparseVector[Double]] = idx._1.toMap;

  val words = idx._2;

  val trainDocLabels = idx._3;

  val labelCounts = idx._4;

  val trainLabelDocs = idx._5;

  val docLength = idx._6;

  val wordIndex = idx._7

  val testDocLabels = idx._8

}

object FeatureBuilder {

  def main(args: Array[String]) = {

    println("Train Data & Labeled Data")

    val testIterator = new ReutersCorpusIterator("test/test-train.zip")
    val labeledIterator = new ReutersCorpusIterator("test/test-labeled.zip")

    val f1: FeatureBuilder = new FeatureBuilder(testIterator, labeledIterator)

    println("features =       " + f1.features)
    println("words =          " + f1.words)
    println("wordIndex =      " + f1.wordIndex)
    println("trainDocLabels = " + f1.trainDocLabels)
    println("lableCounts =    " + f1.labelCounts)
    println("trainLabelDocs = " + f1.trainLabelDocs)
    println("docLength =      " + f1.docLength)

    println("Train Data & Unlabeled Data")

    val testIterator2 = new ReutersCorpusIterator("test/test-train.zip")
    val unlabeledIterator = new ReutersCorpusIterator("test/test-unlabeled.zip")

    val f2: FeatureBuilder = new FeatureBuilder(testIterator2, unlabeledIterator)

    println("features =       " + f2.features)
    println("words =          " + f2.words)
    println("wordIndex =      " + f2.wordIndex)
    println("trainDocLabels = " + f2.trainDocLabels)
    println("lableCounts =    " + f2.labelCounts)
    println("trainLabelDocs = " + f2.trainLabelDocs)
    println("docLength =      " + f2.docLength)

  }

}
