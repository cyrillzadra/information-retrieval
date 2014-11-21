package assignment2.naivebayse

import ch.ethz.dal.classifier.processing.ReutersCorpusIterator
import com.github.aztek.porterstemmer.PorterStemmer
import ch.ethz.dal.classifier.processing.Tokenizer
import assignment2.StopWords
import assignment2.index.IndexBuilder

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
  val testDataLabeledPath = "C:/dev/projects/eth/information-retrieval/course-material/assignment2/training/train_small/";

  val trainDataIter: ReutersCorpusIterator = new ReutersCorpusIterator(trainDataPath)
  val testDataLabeledIter: ReutersCorpusIterator = new ReutersCorpusIterator(testDataLabeledPath)

  val idx : IndexBuilder = new IndexBuilder(trainDataIter)

  println(idx.index.take(20))
  
  for ((t, c) <- idx.topicCounts)
    println(t + ": " + c + " documents")

  println(idx.nrOfDocuments  + " docs in corpus")

  def p(c: String): Double = {
    val p = idx.topicCounts(c).toDouble / idx.nrOfDocuments.toDouble
    println(p)
    p
  }

  //TODO numberOfWords .. should be distinct?
  def pwc(word: String, topic: String, numberOfWords : Int): Double = {   
    //la place smoothing
    val alpha = 1.0
    var x = 0.0;
    if(idx.index.contains(word)) {
      println("word => "  + word)
      println("topic => " + topic)
      println("topic len(d) => " +  idx.index2(topic).sum.toDouble)
      println("word index => " + idx.index(word))
      println("word filtered  index => " + idx.index(word).filter(p => p.category.equals(topic)))
      x = idx.index(word).map(x => x.tf.toDouble + alpha).sum.toDouble / idx.index2(topic).map(len => len.toDouble + alpha * numberOfWords.toDouble).sum.toDouble
    } else {
      x = 0.toDouble
    }  
    println(x)
    x
  }

  def naiveBayse(tokens: List[String], topics: List[String]): List[(String,Double)] = {
    val x = topics.map{ topic =>
      println(topic)
      println ( math.log(p(topic)) + " " + tokens.groupBy(identity).map( t => t._2.size.toDouble 
          * math.log(pwc(t._1, topic, tokens.size))).sum.toDouble )
      topic -> (math.log(p(topic)) + tokens.groupBy(identity).map( word => word._2.size.toDouble * math.log(pwc(word._1, topic, tokens.size))).sum.toDouble)  
    }
    x
  }
  
  def maxArg( r : List[(String, Double)]): String = {
    //return max probability
    r.maxBy(_._2)._1		
  }  

  val doc = StopWords.filterNot(Tokenizer.tokenize(PorterStemmer.stem("reuter"))).toList
  val result = naiveBayse(doc, idx.topicCounts.keySet.toList);
  
  println(result)  
  println(maxArg(result))

}