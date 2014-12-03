package assignment2.naivebayse

import assignment2.Classification
import assignment2.index.IndexBuilder
import assignment2.io.ResultWriter
import assignment2.score.PrecisionRecallF1
import ch.ethz.dal.classifier.processing.ReutersCorpusIterator
import ch.ethz.dal.tinyir.util.StopWatch

class NaiveBayseClassification(trainDataPath: String, testDataLabeledPath: String, labeled: Boolean)
  extends Classification {

  val trainDataIter: ReutersCorpusIterator = new ReutersCorpusIterator(trainDataPath)
  val testDataIter: ReutersCorpusIterator = new ReutersCorpusIterator(testDataLabeledPath)

  val idx: IndexBuilder = new IndexBuilder(trainDataIter)

  def process() = {
    println("Start labeled test data")
    val sw = new StopWatch; sw.start

    val resultScore = scala.collection.mutable.Map[String, PrecisionRecallF1[String]]()
    var progress: Int = 0
    while (testDataIter.hasNext) {
      progress += 1;
      val doc = testDataIter.next

      val result = naiveBayse(doc.tokens, idx.topicCounts.keySet.toList);
      val sortedResult = sortByProbability(result)

      resultScore += doc.name -> new PrecisionRecallF1(sortedResult, doc.topics)
      
      if (progress % 2500 == 0) {
        println("progress = " + progress.toDouble / 50000 * 100 + " % " + " time = " + sw.uptonow)
      }
    }
    sw.stop
    println("Stopped time = " + sw.stopped)
    println("Start writing result")
    new ResultWriter(resultScore.toMap, "nb", labeled).write()

    println("Finished")

  }

  private def naiveBayse(tokens: List[String], topics: List[String]): List[(String, Double)] = {
    val tf = tokens.groupBy(identity);
    val x = topics.par.map { topic =>
      val topicTf: scala.collection.mutable.Map[String, Int] = idx.topicTfIndex(topic)
      val topicLength = idx.trainTopicLength(topic)

      topic -> (math.log(p(topic)) + tf.par.map(word =>
        word._2.size.toDouble * math.log(pwc(word._1, topic, topicTf, topicLength))).sum.toDouble)
    }
    x.toList
  }

  private def pwc(word: String, topic: String, topicTf: scala.collection.mutable.Map[String, Int], labelLength: Int): Double = {
    //la place smoothing
    val alpha = 1.0
    val tf: Int = topicTf.getOrElse(word, 0)
    val r = (tf.toDouble + alpha) / (labelLength.toDouble + alpha * idx.words.size.toDouble)
    r
  }

  def p(c: String): Double = {
    val p = idx.topicCounts(c).toDouble / idx.nrOfDocuments.toDouble
    p
  }

  /**
   * Sorting and then returning only top 3
   */
  private def sortByProbability(r: List[(String, Double)]): Seq[String] = {
    r.sortBy(_._2).reverse.map(f => f._1).toSeq.take(3)
  }

}

object NaiveBayseClassification {

  def main(args: Array[String]) = {

    val trainDataPath = "C:/dev/projects/eth/information-retrieval/course-material/assignment2/training/train/";
    val testDataPath = "C:/dev/projects/eth/information-retrieval/course-material/assignment2/test-with-labels/test-with-labels/";

    val c = new NaiveBayseClassification(trainDataPath, testDataPath, true)

    c.process()
  }

}