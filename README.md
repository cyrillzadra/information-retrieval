information-retrieval
=====================

Run SearchSystem from jar file.

java -jar searchSystem.jar [modelType] [inputPath] [queryPath] [qrlesPath]

+ example for term based model
java -jar searchSystem.jar T C:/temp/docs/ C:/temp/queries.txt C:/temp/qrels.txt

+ example for term language model model
java -jar searchSystem.jar L C:/temp/docs/ C:/temp/queries.txt C:/temp/qrels.txt


Run SearchSystem by importing project to scala eclipse ide first.

Run SearchSystem.scala in Scala IDE by "Run As" -> "Run Configuration" 
and set "Program arguments"  as follow [modelType] [inputPath] [queryPath] [qrlesPath]

example:

T C:/temp/docs/ C:/temp/queries.txt C:/temp/qrels.txt

[modelType] : T for term-based and L for language-based model
[inputPath] : path of folder which contains all documents (extracted from zip files)
[queryPath] : path of file with defined queries.
[qrlesPath] : path of file with judgements

"VM Arguments" have been set to -> -Xss400m -Xms3g -Xmx5g -XX:-UseGCOverheadLimit

How Program works:

1) Initialize a file stream by given [inputPath]. 

2) Loads queries from a given file [queryPath].

	query file should look like:
	[topic]:[query]

	Example with all queries from 51 to 100 should be found in source zip file. Name of file is queries.txt.

3) Depending on given [modelType] it runs 3a) or 3b

3a) For "T" -> Term based model (TdIdf)

	Used relevance model -> tf-idf(w,d) = ( 1 + log(tf(w,d)) ) * log( n / df(w) )

	+ Class assignment.tdidf.TdIdfAlertsTipster first creates idfModel by iterating over Stream which is done in assignment.tdidf.TdIdfIndex.
		
		++  document frequency 		  	: df(t) =  number of documents in the collection that contains term t.
			
			  private val idx: (collection.mutable.Map[String, Int], Int) = {
				//document frequencies
				val df = collection.mutable.Map[String, Int]() ++= qry.map(t => t -> 0)
				var nrOfDocuments: Int = 0;
				for (doc <- docsStream) {
				  df ++= doc.tokens.distinct.filter(t => qry.contains(t)).map(t => t -> (1 + df.getOrElse(t, 0)))
				  nrOfDocuments += + 1
				}
				(df, nrOfDocuments)
			  }
			
		++  inverse document frequency 	: idf(t) = log ( numberOfDocuments / df(t) )
		
			val idf: Map[String, Double] = filteredNumberOfDocmentsByTerm.mapValues(x => math.log(numberOfDocuments / x))
			
	+ After index has been initialized program iterates again over stream evaluates a score for each document.
	
		scoring function :  

			tf-idf(w,d) = ( 1 + log tf(w,d)) * idf(w) )
			
			log2 (n / df(w)) is already evaluated in TdIdfIndex thats why we can use idf(w).

			sum ( tf-idf (w,d) ) 				

3b) For "L" -> Language based model

	Used relevance model -> P(t|d) = lambda * Pmle(t|Md) + (1 − lambda)* Pmle(t|Mc)
		From http://nlp.stanford.edu/IR-book/pdf/irbookonlinereading.pdf (Page 245).

	+ Class assignment.langmodel.LangModelAlertsTipster first creates idfModel by iterating over Stream which is done in assignment.langmodel.LangModelIndex.
		
		++  collection frequency 	: cf(t) =  number of tokens in the collection of term t.
			
			  private val idx: (collection.mutable.Map[String, Int], Int) = {
				val cf = collection.mutable.Map[String, Int]().withDefaultValue(0)					
				var iter = 0
				var nrOfTokens: Int = 0;
				for (doc <- docsStream.stream) {
					cf ++= doc.tokens.distinct.filter(t => qry.contains(t))
							.groupBy(identity).map({ case (term, coll) => term -> (coll.size + cf.getOrElse(term, 0) ) } )
					nrOfTokens += doc.tokens.size
				}
				(cf, nrOfTokens)
			  }s
			
		++  languageModel(t) = cf(t) / nrOfTokens
		
			val tokenFrequencies: Map[String, Double] = idx._1.map(kv => (kv._1, kv._2)).toMap.mapValues(x => x / numberOfTokensInCollection)
			
	+ After index has been initialized program iterates again over stream evaluates a score for each document.
	
	    qterms.map(x => log2(1.0 + (1.0 - lambda) * tfs.getOrElse(x, 0).toDouble + lambda * index.tokenFrequencies(x))).sum

4) If judgments [qrlesPath]	are defined it will calculate PrecisionRecall, AveragePrecision & MeanAveragePrecision from result scores (3a) & 3b))





