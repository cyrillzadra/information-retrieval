package assignment2.index

import ch.ethz.dal.classifier.processing.ReutersCorpusIterator
import ch.ethz.dal.tinyir.util.StopWatch

class IndexBuilder(data: ReutersCorpusIterator) {

  val idx = {
    println("Start building index")
    var topicCounts = scala.collection.mutable.Map[String, Int]()
    var documentCounts = 0.0;
    var trainLabelLength = scala.collection.mutable.Map[String, Int]()
    var topicTfIndex = scala.collection.mutable.Map[String, scala.collection.mutable.Map[String, Int]]()
    var words = Set[String]()

    val sw = new StopWatch; sw.start

    while (data.hasNext) {
      val doc = data.next
      
      documentCounts += 1
      val tfMap = doc.tokens.groupBy(identity);
      val tf = tfMap.mapValues(l => l.length)
      
      words ++= tf.keySet
      
      topicCounts ++= doc.topics.map(c => (c -> (1 + topicCounts.getOrElse(c, 0))))
      trainLabelLength ++= doc.topics.map(c => (c -> (doc.tokens.size + trainLabelLength.getOrElse(c, 0))))

      val tempTopicTf : Map[String, Map[String,Int]] = 
        doc.topics.map(t => t -> tfMap.map(x => (x._1 -> x._2.length))).toMap

      //update global ( topic -> (word -> tf) ) index
      tempTopicTf.map { c =>
        var temp: scala.collection.mutable.Map[String, Int] = topicTfIndex.getOrElse(c._1, scala.collection.mutable.Map[String, Int]())
        temp ++= c._2.map(x => x._1 -> (x._2 + temp.getOrElse(x._1, 0)))
        topicTfIndex(c._1) = temp
      }

      if (documentCounts % 30000 == 0) {
        println("items = " + documentCounts + " time = " + sw.uptonow + " sec")
      }
    }

    sw.stop
    println("Stopped time = " + sw.stopped)

    println(documentCounts + " docs in corpus")
    
    (topicCounts, documentCounts, trainLabelLength, topicTfIndex, words)
  }

  /**
   * Map[topic, number of Topics]
   */
  val topicCounts: scala.collection.mutable.Map[String, Int] = idx._1;

  val nrOfDocuments: Double = idx._2;

  /**
   * topal number of tokens per topic
   */
  val trainTopicLength = idx._3;

  val topicTfIndex = idx._4

  /**
   * Set containing all words
   */
  val words = idx._5

}

object IndexBuilder {

  def main(args: Array[String]) = {

    println("Train Data & Labeled Data")

    val testIterator = new ReutersCorpusIterator("test/test-train.zip")
    val labeledIterator = new ReutersCorpusIterator("test/test-labeled.zip")

    val f1: IndexBuilder = new IndexBuilder(testIterator)

    println("nrOfDocuments =            " + f1.nrOfDocuments)
    println("topicCounts =              " + f1.topicCounts)
    println("trainTopicLength =           " + f1.trainTopicLength)
    println("topicTfIndex =                    " + f1.topicTfIndex)

  }

}
