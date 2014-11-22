package assignment2.naivebayse

import ch.ethz.dal.classifier.processing.ReutersCorpusIterator
import com.github.aztek.porterstemmer.PorterStemmer
import ch.ethz.dal.classifier.processing.Tokenizer
import assignment2.StopWords
import assignment2.index.IndexBuilder
import assignment2.score.PrecisionRecallF1
import assignment.io.ResultWriter

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

  val trainDataPath = "C:/dev/projects/eth/information-retrieval/course-material/assignment2/training/train_small/";
  val testDataLabeledPath = "C:/dev/projects/eth/information-retrieval/course-material/assignment2/test-with-labels/test-with-labels-small/";

  val trainDataIter: ReutersCorpusIterator = new ReutersCorpusIterator(trainDataPath)
  val testDataLabeledIter: ReutersCorpusIterator = new ReutersCorpusIterator(testDataLabeledPath)

  println("Start building index")
  val idx: IndexBuilder = new IndexBuilder(trainDataIter)

  println(idx.nrOfDocuments + " docs in corpus") 
  
  println("Start training")
  
  val resultScore = scala.collection.mutable.Map[String, PrecisionRecallF1[String]]()
  var progress: Int = 0
  while (testDataLabeledIter.hasNext) {
    progress += 1;
    val doc = testDataLabeledIter.next
    val result = naiveBayse(doc.tokens, idx.topicCounts.keySet.toList);
    val sortedResult = sortByProbability(result)
    resultScore += doc.name -> new PrecisionRecallF1(sortedResult, doc.topics)

    if (progress % 1000 == 0) {
      println("progress = " + progress.toDouble / 50000 * 100 + " % ")
    }

  }
  
  println("Start writing result")

  new ResultWriter("classify-cyrill-zadra-l-nb.run", resultScore).write()
  
  println("Finished")

  private def naiveBayse(tokens: List[String], topics: List[String]): List[(String, Double)] = {
    val x = topics.map { topic =>
      //println(topic)
//      println(math.log(p(topic)) + " " + tokens.groupBy(identity).map(t => t._2.size.toDouble
//        * math.log(pwc(t._1, topic, tokens.size))).sum.toDouble)
      topic -> (idx.pcIndex(topic) + tokens.groupBy(identity).map(word =>
        word._2.size.toDouble * math.log(pwc(word._1, topic, tokens.size))).sum.toDouble)
    }
    x
  }

  private def maxArg(r: List[(String, Double)]): String = {
    //return max probability
    r.maxBy(_._2)._1
  }
  
  //TODO how many 
  private def sortByProbability(r: List[(String, Double)]): Seq[String] = {
    //return max probability
    r.sortBy(_._2).map(f => f._1).toSeq.take(5)
  }

  //TODO numberOfWords .. should it be distinct?
  private def pwc(word: String, topic: String, numberOfWords: Int): Double = {
    //la place smoothing
//    val alpha = 1.0
    var pwc = 0.0;
    if (idx.index.contains(word)) {
        pwc = idx.index(word).map(x => x.tf.toDouble).sum.toDouble / idx.numberOfTokensPerTopic2(topic).toDouble
    } else {
      pwc = 0.toDouble
    }
    pwc
  }

}