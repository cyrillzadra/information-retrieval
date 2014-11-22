package assignment2.naivebayse

import assignment.io.ResultWriter
import assignment2.index.FeatureBuilder
import assignment2.score.PrecisionRecallF1
import breeze.linalg.SparseVector
import ch.ethz.dal.classifier.processing.ReutersCorpusIterator
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

  val trainDataPath = "C:/dev/projects/eth/information-retrieval/course-material/assignment2/training/train-small/";
  val testDataLabeledPath = "C:/dev/projects/eth/information-retrieval/course-material/assignment2/test-with-labels/test-with-labels-small/";

  val trainDataIter: ReutersCorpusIterator = new ReutersCorpusIterator(trainDataPath)
  val testDataLabeledIter: ReutersCorpusIterator = new ReutersCorpusIterator(testDataLabeledPath)
  val sw = new StopWatch; sw.start
  println("Start building index")
  val idx: FeatureBuilder = new FeatureBuilder(trainDataIter, testDataLabeledIter)

  println(" time = " + sw.uptonow)

  println("Start labeled test data")

  val testFeatures = idx.testDocLabels;

  var progress: Int = 0

  val resultScore = testFeatures.map {
    x =>
      val f = idx.features(x._1);
      val result = naiveBayse(f, idx.labelCounts.keySet.toList);
      val sortedResult = sortByProbability(result)
      
      progress += 1
      if (progress % 100 == 0) {
        println("progress = " + progress.toDouble / 50000 * 100 + " % " + " time = " + sw.uptonow)
      }     
      
      (x._1 , new PrecisionRecallF1(sortedResult, idx.testDocLabels(x._1).toSet))
  }

  sw.stop
  println("Stopped time = " + sw.stopped)
  println("Start writing result")
  new ResultWriter("classify-cyrill-zadra-l-nb.run", resultScore.toMap).write()

  println("Start unlabeled test data")
  //TODO
  println("Finished")

  private def naiveBayse(doc: SparseVector[Double], topics: List[String]): List[(String, Double)] = {
    val x = topics.par.map { topic =>
      val features: Map[String, SparseVector[Double]] =
        idx.trainLabelDocs(topic).map(doc => (doc -> idx.features(doc))).toMap

      topic -> (math.log(p(topic)) +
        doc.mapActivePairs((k, v) => v * math.log(pwc(k, features, topic, doc.sum.toInt))).sum.toDouble)
    }
    x
  }

  private def pwc(wordIndex: Int, features: Map[String, SparseVector[Double]],
                  topic: String, numberOfWords: Int): Double = {

    //la place smoothing
    val alpha = 1.0
    var x = 0.0;
    var y = 0.0;
    features.map {
      f =>
        x += f._2(wordIndex) + alpha
        y += idx.docLength(f._1) + alpha * numberOfWords.toDouble
    }
    (x / y)
  }

  def p(c: String): Double = {
    val p = idx.labelCounts(c).toDouble / idx.features.size.toDouble
    p
  }

  /**
   * return max 5 probability
   *
   */
  private def maxArg(r: List[(String, Double)]): String = {
    r.maxBy(_._2)._1
  }

  /**
   * sorty by probablity
   */
  private def sortByProbability(r: List[(String, Double)]): Seq[String] = {
    val sorted = r.sortBy(_._2).reverse.map(f => f._1).toSeq.take(5)
    sorted
  }

}