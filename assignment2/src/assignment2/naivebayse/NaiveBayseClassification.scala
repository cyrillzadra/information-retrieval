package assignment2.naivebayse

import ch.ethz.dal.classifier.processing.ReutersCorpusIterator
import com.github.aztek.porterstemmer.PorterStemmer
import ch.ethz.dal.classifier.processing.Tokenizer
import assignment2.StopWords

case class FreqResult(val category: String, val tf: Int) {
  override def equals(obj: Any): Boolean = obj match {
    case myCase: FreqResult => category.equals(myCase.category)
    case _ => false
  }
}

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
  val testDataLabeledIter: ReutersCorpusIterator = new ReutersCorpusIterator(trainDataPath)

  //k = category
  val topicCounts = scala.collection.mutable.Map[String, Int]()
  var count = 0;
  var length = 0;
  //k = word  
  val index = scala.collection.mutable.Map[String, List[FreqResult]]()
  //k = category
  val index2 = scala.collection.mutable.Map[String, List[Int]]()
  while (trainDataIter.hasNext) {
    val doc = trainDataIter.next
    topicCounts ++= doc.topics.map(c => (c -> (1 + topicCounts.getOrElse(c, 0))))

    length += doc.tokens.length;
    index2 ++= doc.topics.map(c => (c -> (List(doc.tokens.length) ++ index2.getOrElse(c, List[Int]()))))

    val s: Map[String, List[FreqResult]] = doc.tokens.groupBy(identity).map(x => x._1 ->
      doc.topics.map(t => new FreqResult(t, x._2.length)).toList)

    index ++= s.map(c => (c._1 -> (c._2 ++ index.getOrElse[List[FreqResult]](c._1, List()))))
    count += 1

    if (count % 10000 == 0) {
      index ++= index.mapValues(v => v.groupBy(identity).mapValues(x => x.reduce((a, b) =>
        FreqResult(a.category, a.tf + b.tf))).values.toList)
      println(index.size)
      println(count.toDouble / 200000 * 100 + " % ")
    }

  }

  println(index.take(20))
  
  for ((t, c) <- topicCounts)
    println(t + ": " + c + " documents")

  println(count + " docs in corpus")

  def p(c: String): Double = {
    val p = topicCounts(c).toDouble / count.toDouble
    println(p)
    p
  }

  def pwc(word: String, c: String): Double = {
    var x = 0.0;
    if(index.contains(word)) 
      x = index(word).filter(p => p.category.equals(c)).map(x => x.tf).sum.toDouble / index2(c).sum.toDouble
    else
      x = 0.toDouble
      
    println(x)
    x
  }

  def naiveBayse(tokens: List[String], topics: List[String]): List[(String,Double)] = {
    val x = topics.map(token =>      
      token -> (math.log(p(token)) + tokens.groupBy(identity).map( t => t._2.size.toDouble * math.log(pwc(t._1, token))).sum.toDouble)  
    )
    x
  }
  
  def maxArg( r : List[(String, Double)]): String = {
    //return max probability
    r.maxBy(_._2)._1		
  }  

  val doc = StopWords.filterNot(Tokenizer.tokenize(PorterStemmer.stem("this is just some reuter test text document and computer"))).toList

  val result = naiveBayse(doc, topicCounts.keySet.toList);
  
  println(result)  
  println(maxArg(result))

}