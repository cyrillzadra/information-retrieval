information-retrieval
=====================

Run SearchSystem.scala in Scala IDE by "Run As" -> "Run Configuration" 
and set "Program arguments"  as follow [modelType] [inputPath] [queryPath] [qrlesPath]

example:

T C:/temp/docs/ C:/temp/topics.txt C:/temp/qrels.txt

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

	Used relefance model -> tf-idf( w, d ) = ( 1 + log tf( w, d)) * log( n / df(w) )

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

			tf-idf( w, d ) = ( 1 + log tf( w, d )) * idf(w) )
			
			log (n / df(w)) is already evaluated in TdIdfIndex thats why we can use idf(w).

			sum ( tf-idf ( w, d ) ) 				

3b) For "L" -> Language based model

	+ Class assignment.langmodel.LangModelAlertsTipster first creates idfModel by iterating over Stream which is done in assignment.langmodel.LangModelIndex.
		
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

4) If judgments [qrlesPath]	are defined it will calculate 
	PrecisionRecall, AveragePrecision & MeanAveragePrecision
	with resulting scores from 3a) or 3b).





