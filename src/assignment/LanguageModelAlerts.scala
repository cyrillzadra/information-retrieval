package assignment

import scala.collection.mutable.PriorityQueue
import scala.math.Ordering.Implicits._

case class ScoredResult (title : String, score: Double)

class ModelAlerts (q: String, n: Int,  lambda : Double) {

  val query = new LanguageModelQuery(q, lambda)
  
  // score a document and try to add to results
  def process(title: String, doc: List[String], index : FreqIndex) : Boolean = {
    val score = query.score(doc, index)
    add(ScoredResult(title,score))
  }
  
  // get top n results (or m<n, if not enough docs processed)
  def results = heap.toList.sortBy(res => -res.score)    

    // heap and operations on heap
  private val heap = new PriorityQueue[ScoredResult]()(Ordering.by(score))
  private def score (res: ScoredResult) = -res.score 
  private def add(res: ScoredResult) : Boolean = {    
    if (heap.size < n)  { // heap not full
      heap += res
      true
    } else if (heap.head.score < res.score) {
        heap.dequeue
        heap += res
        true
      } else false
  }
  

}

