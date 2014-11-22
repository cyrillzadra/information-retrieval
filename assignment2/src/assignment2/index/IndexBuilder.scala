package assignment2.index

import ch.ethz.dal.classifier.processing.ReutersCorpusIterator
import ch.ethz.dal.tinyir.util.StopWatch

case class Postings(val topic: String, val tf: Int)

class IndexBuilder(data: ReutersCorpusIterator) {
  
  val NUMBER_OF_DOCUMENTS = 200000;

  val idx = {
    val topicCounts = scala.collection.mutable.Map[String, Int]()
    var documentCounts = 0.0;
    var tokenCounts = 0.0;
    val index = scala.collection.mutable.Map[String, List[Postings]]()
    val numberOfTokensPerTopic = scala.collection.mutable.Map[String, List[Int]]()
    val sw = new StopWatch; sw.start

    while (data.hasNext) {
      val doc = data.next
      tokenCounts += doc.tokens.length;
      documentCounts += 1

      topicCounts ++= doc.topics.map(c => (c -> (1 + topicCounts.getOrElse(c, 0))))

      val s: Map[String, List[Postings]] = doc.tokens.groupBy(identity).map(x => x._1 ->
        doc.topics.map(t => new Postings(t, x._2.length)).toList)
      //Map[word,List(Postings(Topic,TermFrequencies)]
      index ++= s.map(c => (c._1 -> (c._2 ++ index.getOrElse[List[Postings]](c._1, List()))))

      //Map[Topic,NumberOfTokens]
      numberOfTokensPerTopic ++= doc.topics.map(c =>
        (c -> (List(doc.tokens.length) ++ numberOfTokensPerTopic.getOrElse(c, List[Int]()))))

      if (documentCounts % 10000 == 0) {
        index ++= index.mapValues(v => v.groupBy(identity).mapValues(x => x.reduce((a, b) =>
          Postings(a.topic, a.tf + b.tf))).values.toList)
        println(index.size)
        println(documentCounts.toDouble / NUMBER_OF_DOCUMENTS * 100 + " % " + " time = " + sw.uptonow)
      }

    }

    sw.stop
    println("Stopped time = " + sw.stopped)

    (topicCounts, documentCounts, tokenCounts, index, numberOfTokensPerTopic)
  }

  val topicCounts: scala.collection.mutable.Map[String, Int] = idx._1;

  val nrOfDocuments: Double = idx._2;

  val nrOfTokens: Double = idx._3;

  val index: scala.collection.mutable.Map[String, List[Postings]] = idx._4;

  val numberOfTokensPerTopic: scala.collection.mutable.Map[String, List[Int]] = idx._5;

  val numberOfTokensPerTopic2: scala.collection.Map[String, Int] =
    numberOfTokensPerTopic.mapValues(x => x.reduce(_ + _))

  val pcIndex = topicCounts.mapValues(x => math.log(x / nrOfDocuments))

}