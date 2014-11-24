package assignment2.index

import ch.ethz.dal.classifier.processing.ReutersCorpusIterator
import ch.ethz.dal.tinyir.util.StopWatch

case class Postings(val word: String, val tf: Int)

class IndexBuilder(data: ReutersCorpusIterator) {

  val NUMBER_OF_DOCUMENTS = 200000;

  val idx = {
    println("Start building index")
    val topicCounts = scala.collection.mutable.Map[String, Int]()
    var documentCounts = 0.0;
    val trainLabelLength = scala.collection.mutable.Map[String, Int]()
    val index2 = scala.collection.mutable.Map[String, scala.collection.mutable.Map[String, Int]]()

    val sw = new StopWatch; sw.start

    while (data.hasNext) {
      val doc = data.next
      documentCounts += 1

      val tfMap = doc.tokens.groupBy(identity);
      val tf = tfMap.mapValues(l => l.length)
      topicCounts ++= doc.topics.map(c => (c -> (1 + topicCounts.getOrElse(c, 0))))

      trainLabelLength ++= doc.topics.map(c => (c -> (doc.tokens.size + trainLabelLength.getOrElse(c, 0))))

      val y = tfMap.map(x => (x._1 -> x._2.length))
      val x = doc.topics.map(t => t -> y).toMap

      x.map { c =>
        var temp: scala.collection.mutable.Map[String, Int] = index2.getOrElse(c._1, scala.collection.mutable.Map[String, Int]())
        temp ++= c._2.map(x => x._1 -> (x._2 + temp.getOrElse(x._1, 0)))
        index2(c._1) = temp

      }

      if (documentCounts % 10000 == 0) {
        println(index2.size)
        println(documentCounts.toDouble / NUMBER_OF_DOCUMENTS * 100 + " % " + " time = " + sw.uptonow)
      }

    }

    sw.stop
    println("Stopped time = " + sw.stopped)

    println(documentCounts + " docs in corpus")
    
    (topicCounts, documentCounts, trainLabelLength, index2)
  }

  val topicCounts: scala.collection.mutable.Map[String, Int] = idx._1;

  val nrOfDocuments: Double = idx._2;

  val trainLabelLength = idx._3;

  val index2 = idx._4

}

object IndexBuilder {

  def main(args: Array[String]) = {

    println("Train Data & Labeled Data")

    val testIterator = new ReutersCorpusIterator("test/test-train.zip")
    val labeledIterator = new ReutersCorpusIterator("test/test-labeled.zip")

    val f1: IndexBuilder = new IndexBuilder(testIterator)

    println("nrOfDocuments =            " + f1.nrOfDocuments)
    println("topicCounts =              " + f1.topicCounts)
    println("trainLabelLength =           " + f1.trainLabelLength)
    println("index2 =                    " + f1.index2)

  }

}
