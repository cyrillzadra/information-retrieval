package assignment

import ch.ethz.dal.tinyir.processing.Document
import ch.ethz.dal.tinyir.processing.StringDocument

case class FreqResult(val tf: List[Int]) {
  def matched(that: FreqResult) = FreqResult(this.tf ::: that.tf)
}

class FreqIndex(docs: Stream[Document]) {

  var nrOfTokensInCollection = 0;

  case class FreqPosting(val id: Int, val freq: Int) extends Ordered[FreqPosting] {
    def compare(that: FreqPosting) = this.id compare that.id
  }

  type PostList = List[FreqPosting]

  val index: Map[String, PostList] = {
    val groupedTuples = postings(docs).groupBy(_.term)
    groupedTuples.mapValues(_.map(tfT => FreqPosting(tfT.id, tfT.count)).sorted)
  }

  case class TfTuple(term: String, id:Int, count: Int)
  
  private def postings(s: Stream[Document]): List[TfTuple] = {
    val nrOfTokensInStream = s.map(d => d.tokens.size)
    //nrOfTokensInCollection += nrOfTokensInStream.toList.sum;
    s.flatMap(d => d.tokens.groupBy(identity)
      .map { case (tk, lst) => TfTuple(tk, d.ID, lst.length) }).toList
  }

  def results(terms: Seq[String]): List[Int] = {
    val resultLists = terms.map(term => results(term))
    val shortToLongLists = resultLists
    val x = shortToLongLists.toList.map(x => x.flatMap(y => y.tf)).filter(p => !p.isEmpty)
    if (x.isEmpty) List(0) else x.map(z => z.reduce(_ + _))
  }

  def results(term: String): List[FreqResult] = {
    index.getOrElse(term, Nil).map(p => FreqResult(List(p.freq)))
  }
}

class TestDocument(val id: String, val s: String) extends Document {
  def title = ""
  def body = s
  def name = ""
  override def ID = id.hashCode()
  def date = ""
  def content = body
}

object FreqIndex {
  def main(args: Array[String]) = {
    val d1 = new TestDocument("1", "mr sherlock holmes who was usually very late")
    val d0 = new TestDocument("0", "i can tell a moriaty when i see one said holmes")
    val d3 = new TestDocument("0", "i can telling a moriaty when i see one said holmes")
    val stream: Stream[TestDocument] = List(d3, d1, d0).toStream
    val idx = new FreqIndex(stream)
    idx.index.foreach { case (d, lst) => println(d + ": " + lst.mkString(" ")) }
    val q = List("a", "i", "who")
    println(q.mkString(" ") + " = " + idx.results(q).mkString(" "))
    println("Total Terms" + idx.nrOfTokensInCollection)
  }
}
