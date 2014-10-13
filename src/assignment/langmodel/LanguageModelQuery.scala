package assignment.langmodel

import ch.ethz.dal.tinyir.processing.Tokenizer
import assignment.FreqIndex

/*
 * lambda value 0.1 for title queries and 0.7 for long queries.
 */
class LanguageModelQuery (query: String, lambda : Double) {  
  val qterms = Tokenizer.tokenize(query).distinct
  val length = qterms.length
    
  def score (doc: List[String], index : FreqIndex) : Double = {
    var numberOfTermsInDocument = doc.size
    
    val tfs : Map[String,Int]= doc.groupBy(identity).mapValues(l => l.length)    
    val qtfs = qterms.flatMap(q => tfs.get(q)).isEmpty match {
	  case true => List(0) 
	  case _ => qterms.flatMap(q => tfs.get(q))
	}
    
    //val qtfs2 = index.results(qterms)
    
    val pPqMd = qtfs.map( x=> x / numberOfTermsInDocument).reduce(_*_)
    //val pPqMc= qtfs2.map( x=> x / index.nrOfTokensInCollection ).reduce(_*_)

    //((1 - lambda) * pPqMc ) + (lambda * pPqMd)
    pPqMd
  }
}