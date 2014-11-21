package assignment2.index

import ch.ethz.dal.classifier.processing.ReutersCorpusIterator

case class Postings(val category: String, val tf: Int) {
  override def equals(obj: Any): Boolean = obj match {
    case myCase: Postings => category.equals(myCase.category)
    case _ => false
  }
}

class IndexBuilder(data: ReutersCorpusIterator) {

  val idx = {
    val topicCounts = scala.collection.mutable.Map[String, Int]()
    var count = 0.0;
    var length = 0.0;
    //k = word  
    val index = scala.collection.mutable.Map[String, List[Postings]]()
    //k = category
    val index2 = scala.collection.mutable.Map[String, List[Int]]()
    while (data.hasNext) {
      val doc = data.next
      topicCounts ++= doc.topics.map(c => (c -> (1 + topicCounts.getOrElse(c, 0))))

      length += doc.tokens.length;
      index2 ++= doc.topics.map(c => (c -> (List(doc.tokens.length) ++ index2.getOrElse(c, List[Int]()))))

      val s: Map[String, List[Postings]] = doc.tokens.groupBy(identity).map(x => x._1 ->
        doc.topics.map(t => new Postings(t, x._2.length)).toList)

      index ++= s.map(c => (c._1 -> (c._2 ++ index.getOrElse[List[Postings]](c._1, List()))))
      count += 1

      if (count % 10000 == 0) {
        index ++= index.mapValues(v => v.groupBy(identity).mapValues(x => x.reduce((a, b) =>
          Postings(a.category, a.tf + b.tf))).values.toList)
        println(index.size)
        println(count.toDouble / 200000 * 100 + " % ")
      }

    }

    (topicCounts, count, length, index, index2)
  }

  val topicCounts: scala.collection.mutable.Map[String, Int] = idx._1;

  val nrOfDocuments: Double = idx._2;

  val nrOfTokens: Double = idx._3;
  
  val index: scala.collection.mutable.Map[String, List[Postings]] = idx._4;
  
  val index2: scala.collection.mutable.Map[String, List[Int]] = idx._5;

}