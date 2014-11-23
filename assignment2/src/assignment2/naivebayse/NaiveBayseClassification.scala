package assignment2.naivebayse

import ch.ethz.dal.classifier.processing.ReutersCorpusIterator
import com.github.aztek.porterstemmer.PorterStemmer
import ch.ethz.dal.classifier.processing.Tokenizer
import assignment2.StopWords
import assignment2.index.IndexBuilder
import assignment2.score.PrecisionRecallF1
import assignment2.io.ResultWriter
import ch.ethz.dal.tinyir.util.StopWatch

/**
 * Build a document classification system that:
 * I parses a document collection and extracts doc features
 * I handles multiple topic labels per training document
 * I assignes multiple topic labels for a test document using three
 * dierent approaches : Naive Bayes, Logistic Regression, SVM
 * I computes precision, recall and F1 score for a test collection of
 * documents
 */
object NaiveBayseClassification extends App {

  val trainDataPath = "C:/dev/projects/eth/information-retrieval/course-material/assignment2/training/train/";
  val testDataLabeledPath = "C:/dev/projects/eth/information-retrieval/course-material/assignment2/test-with-labels/test-with-labels/";

  val trainDataIter: ReutersCorpusIterator = new ReutersCorpusIterator(trainDataPath)
  val testDataLabeledIter: ReutersCorpusIterator = new ReutersCorpusIterator(testDataLabeledPath)

  println("Start building index")
  val idx: IndexBuilder = new IndexBuilder(trainDataIter)

  println(idx.nrOfDocuments + " docs in corpus")

  println("Start labeled test data")
  val sw = new StopWatch; sw.start

  val resultScore = scala.collection.mutable.Map[String, PrecisionRecallF1[String]]()
  var progress: Int = 0
  while (testDataLabeledIter.hasNext) {
    progress += 1;
    val doc = testDataLabeledIter.next

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
  new ResultWriter("classify-cyrill-zadra-l-nb.run", resultScore.toMap, true).write()

  println("Start unlabeled test data")

  println("Finished")

  private def naiveBayse(tokens: List[String], topics: List[String]): List[(String, Double)] = {
    val tf = tokens.groupBy(identity);
    val x = topics.par.map { topic =>
      val topicTf: scala.collection.mutable.Map[String, Int] = idx.index2(topic)
      val labelLength = idx.trainLabelLength(topic)

      topic -> (math.log(p(topic)) + tf.par.map(word =>
        word._2.size.toDouble * math.log(pwc(word._1, topic, tokens.size, topicTf, labelLength))).sum.toDouble)
    }
    x.toList
  }

  private def pwc(word: String, topic: String, numberOfWords: Int, topicTf: scala.collection.mutable.Map[String, Int], labelLength: Int): Double = {
    //la place smoothing
    val alpha = 1.0
    val tf: Int = topicTf.getOrElse(word, 0)

    (tf.toDouble + alpha) / (labelLength.toDouble + alpha * numberOfWords.toDouble)
  }

  def p(c: String): Double = {
    val p = idx.topicCounts(c).toDouble / idx.nrOfDocuments.toDouble
    p
  }

  /**
   *
   */
  private def sortByProbability(r: List[(String, Double)]): Seq[String] = {
    r.sortBy(_._2).reverse.map(f => f._1).toSeq.take(5)
  }

}