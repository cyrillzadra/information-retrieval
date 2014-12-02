package assignment2.index

import breeze.linalg.SparseVector
import ch.ethz.dal.classifier.processing.ReutersCorpusIterator
import ch.ethz.dal.tinyir.util.StopWatch

class FeatureBuilder(train: ReutersCorpusIterator, test: ReutersCorpusIterator) {

  val idx = {
    println("Start Feature Builder")

    val docs = scala.collection.mutable.Map[String, List[(String, Int)]]()
    val trainDocLength = scala.collection.mutable.Map[String, Int]()
    val trainDocLabels = scala.collection.mutable.Map[String, List[String]]()
    val trainLabelDocs = scala.collection.mutable.Map[String, List[String]]()

    val testDocLabels = scala.collection.mutable.Map[String, List[String]]()
    val labelCounts = scala.collection.mutable.Map[String, Int]()
    val words = scala.collection.mutable.Set[String]()
    val trainWords = scala.collection.mutable.Set[String]()

    println("Training data")
    val sw = new StopWatch; sw.start
    var cnt: Int = 0;
    while (train.hasNext) {
      val doc = train.next
      val tf = doc.tokens.groupBy(identity).mapValues(l => l.length).toList
      docs += (doc.name -> tf)
      trainDocLength += (doc.name -> doc.tokens.size)
      words ++= tf.map(c => c._1)
      trainWords ++= tf.map(c => c._1)
      trainDocLabels += (doc.name -> doc.topics.toList)
      trainLabelDocs ++= doc.topics.map(c => (c -> (List(doc.name) ++ trainLabelDocs.getOrElse(c, List()))))
      labelCounts ++= doc.topics.map(c => (c -> (1 + labelCounts.getOrElse(c, 0))))
      cnt += 1;
      if (cnt % 30000 == 0) {
        println("items = " + cnt + " time = " + sw.uptonow + " sec")
      }
    }
    sw.stop
    sw.start
    cnt = 0
    println("Test data")
    while (test.hasNext) {
      val doc = test.next
      val tf = doc.tokens.groupBy(identity).mapValues(l => l.length)
      docs += (doc.name -> tf.toList)
      words ++= tf.map(c => c._1)
      testDocLabels += (doc.name -> doc.topics.toList)
      cnt += 1;
      if (cnt % 5000 == 0) {
        println("items = " + cnt + " time = " + sw.uptonow + " sec")
      }
    }
    sw.stop

    println("train docs size = " + docs.size + " ::: ")
    //println(docs.take(10))
    println("train words size = " + words.size + " ::: ")
    //println(words.take(10))
    println("train trainDocLabels size = " + trainDocLabels.size)
    //println(trainDocLabels.take(10))
    println("train trainLabelDocs size = " + trainLabelDocs.size)
    //println(trainLabelDocs.take(10))

    val dim = words.size
    val dim_y = docs.size

    val wordSeq = words.toSeq
    val wordIndex = words.zipWithIndex.toMap

    //build feature vector
    var features = scala.collection.mutable.Map[String, SparseVector[Double]]()
    for (d <- docs) {
      val v = SparseVector.zeros[Double](dim)
      d._2.map(word => v(wordIndex(word._1)) = word._2)
      features += d._1 -> v
    }
    println("Finish Feature Builder")
    (features, wordIndex, trainDocLabels, labelCounts, trainLabelDocs, trainDocLength, wordSeq, testDocLabels, trainWords)
  }

  val features: Map[String, SparseVector[Double]] = idx._1.toMap;

  val words = idx._2;

  val trainDocLabels = idx._3;

  val labelCounts = idx._4;

  val trainLabelDocs = idx._5;

  val docLength = idx._6;

  val wordIndex = idx._7

  val testDocLabels: scala.collection.mutable.Map[String, List[String]] = idx._8

  val trainWords: Set[String] = idx._9.toSet

  val dim: Int = words.size

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
    println("lableCounts =    " + f1.labelCounts)
    println("trainWords =     " + f1.trainWords)
    println("trainDocLabels = " + f1.trainDocLabels)
    println("trainLabelDocs = " + f1.trainLabelDocs)
    println("docLength =      " + f1.docLength)
    println("testDocLabels =  " + f1.testDocLabels)

    println("Train Data & Unlabeled Data")

    val testIterator2 = new ReutersCorpusIterator("test/test-train.zip")
    val unlabeledIterator = new ReutersCorpusIterator("test/test-unlabeled.zip")

    val f2: FeatureBuilder = new FeatureBuilder(testIterator2, unlabeledIterator)

    println("features =       " + f2.features)
    println("words =          " + f2.words)
    println("wordIndex =      " + f2.wordIndex)
    println("lableCounts =    " + f2.labelCounts)
    println("trainWords =     " + f2.trainWords)
    println("trainDocLabels = " + f2.trainDocLabels)
    println("trainLabelDocs = " + f2.trainLabelDocs)
    println("docLength =      " + f2.docLength)
    println("testDocLabels =  " + f2.testDocLabels)

  }

}
