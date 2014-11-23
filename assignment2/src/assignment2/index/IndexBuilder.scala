package assignment2.index

import ch.ethz.dal.classifier.processing.ReutersCorpusIterator
import ch.ethz.dal.tinyir.util.StopWatch

case class Postings(val topic: String, val tf: Int)

class IndexBuilder(data: ReutersCorpusIterator) {

  val NUMBER_OF_DOCUMENTS = 200000;

  val idx = {
    val docs = scala.collection.mutable.Map[String, Map[String, Int]]()
    val topicCounts = scala.collection.mutable.Map[String, Int]()
    var documentCounts = 0.0;
    val trainLabelDocs = scala.collection.mutable.Map[String, List[String]]()
    val trainDocLength = scala.collection.mutable.Map[String, Int]()
    val index = scala.collection.mutable.Map[String, List[Postings]]()

    val sw = new StopWatch; sw.start

    while (data.hasNext) {
      val doc = data.next
      documentCounts += 1

      val tf = doc.tokens.groupBy(identity).mapValues(l => l.length)
      //docs += (doc.name -> tf.toMap)
      trainDocLength += (doc.name -> doc.tokens.size)
      topicCounts ++= doc.topics.map(c => (c -> (1 + topicCounts.getOrElse(c, 0))))
      trainLabelDocs ++= doc.topics.map(c => (c -> (List(doc.name) ++ trainLabelDocs.getOrElse(c, List()))))

      val s: Map[String, List[Postings]] = doc.tokens.groupBy(identity).map(x => x._1 ->
        doc.topics.map(t => new Postings(t, x._2.length)).toList)
      
      //Map[word,List(Postings(Topic,TermFrequencies)]
      index ++= s.map(c => (c._1 -> (c._2 ++ index.getOrElse[List[Postings]](c._1, List()))))

      if (documentCounts % 10000 == 0) {
        index ++= index.mapValues(v => v.groupBy(identity).mapValues(x => x.reduce((a, b) =>
          Postings(a.topic, a.tf + b.tf))).values.toList)

        println(docs.size)
        println(docs.take(2))
        println(documentCounts.toDouble / NUMBER_OF_DOCUMENTS * 100 + " % " + " time = " + sw.uptonow)
      }

    }

    sw.stop
    println("Stopped time = " + sw.stopped)

    (topicCounts, documentCounts, index, 0, 0, trainLabelDocs, docs, trainDocLength)
  }

  val topicCounts: scala.collection.mutable.Map[String, Int] = idx._1;

  val nrOfDocuments: Double = idx._2;

  val index = idx._4

  val trainLabelDocs = idx._6

  val trainDocs = idx._7

  val trainDocLength = idx._8

}

object IndexBuilder {

  def main(args: Array[String]) = {

    println("Train Data & Labeled Data")

    val testIterator = new ReutersCorpusIterator("test/test-train.zip")
    val labeledIterator = new ReutersCorpusIterator("test/test-labeled.zip")

    val f1: IndexBuilder = new IndexBuilder(testIterator)

    println("nrOfDocuments =            " + f1.nrOfDocuments)
    println("topicCounts =              " + f1.topicCounts)
    println("trainLabelDocs =           " + f1.trainLabelDocs)
    println("trainDocs =                " + f1.trainDocs)
    println("trainDocLength =           " + f1.trainDocLength)

  }

}
