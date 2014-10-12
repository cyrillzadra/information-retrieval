package assignment

import ch.ethz.dal.tinyir.processing.Tokenizer

/*
 * 
 */
class LanguageModelQuery (query: String, lambda : Double) {  
  val qterms = Tokenizer.tokenize(query).distinct
  val length = qterms.length
    
  def score (doc: List[String]) : Double = {
    val tfs : Map[String,Int]= doc.groupBy(identity).mapValues(l => l.length)    
    
    val qtfs = qterms.flatMap(q => tfs.get(q))
    var numberOfTokensInDocument = doc.size
    val numTermsInCommon = qtfs.length 
    val docLen = tfs.values.map(x => x*x).sum.toDouble  // Euclidian norm
    val queryLen = qterms.length.toDouble  
    val termOverlap = qtfs.sum.toDouble / (docLen * queryLen)
    

    ((1 - lambda) * numTermsInCommon / numberOfTokensInDocument) + (lambda * termOverlap / numberOfTokensInDocument)
  }
}