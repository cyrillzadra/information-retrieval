package assignment2.naivebayse

import ch.ethz.dal.classifier.processing.ReutersCorpusIterator

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

  val trainDataPath = "C:/dev/projects/eth/information-retrieval/course-material/assignment2/training/train/";

  val iter: ReutersCorpusIterator = new ReutersCorpusIterator(trainDataPath)

  val topicCounts = scala.collection.mutable.Map[String, Int]()
  var count = 0;
  val index = scala.collection.mutable.Map[String, List[FreqResult]]()
  val dic =
    while (iter.hasNext) {
      val doc = iter.next
      topicCounts ++= doc.topics.map(c => (c -> (1 + topicCounts.getOrElse(c, 0))))

      val s: Map[String, List[FreqResult]] = doc.tokens.groupBy(identity).map(x => x._1 ->
        doc.topics.map(t => new FreqResult(t, x._2.length)).toList)

      index ++= s.map(c => (c._1 -> (c._2 ++ index.getOrElse[List[FreqResult]](c._1, List()))))

      count += 1

      if (count % 10000 == 0) {
        index ++= index.mapValues(v => v.groupBy(identity).mapValues(x => x.reduce((a, b) => FreqResult(a.category, a.tf + b.tf))).values.toList)
        println(index.size)
        println(count.toDouble / 200000 * 100 + " % ")
      }
    }

  for ((t, c) <- topicCounts)
    println(t + ": " + c + " documents")

  println(count + " docs in corpus")

  def p(c: String): Double =
    topicCounts(c) / count
    
  def pwc(word : String , c : String ) : Double = 
    

}