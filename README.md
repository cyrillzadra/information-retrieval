information-retrieval
=====================

Run SearchSystem.scala in Scala IDE by "Run As" -> "Run Configuration" 
and set "Program arguments"  as follow [modelType] [inputPath] [qrlesPath]

example:

T C:/temp/docs/ C:/temp/qrels

[modelType] : T for term-based and L for language-based model
[inputPath] : path of folder which contains all documents
[qrlesPath] : path of file with judgements

How Program works

1) Initialize a stream by given inputPath.

2) Loads pre-defined queries.

  91 -> "U.S. Army Acquisition of Advanced Weapons Systems"
  92 -> "International Military Equipment Sales"
  93 -> "What Backing Does the National Rifle Association Have?"
  94 -> "Computer-aided Crime"
  95 -> "Computer-aided Crime Detection"
  96 -> "Computer-Aided Medical Diagnosis"
  97 -> "Fiber Optics Applications"
  98 -> "Fiber Optics Equipment Manufacturers"
  99 -> "Iran-Contra Affair"
  100 -> "Controlling the Transfer of High Technology"

3) Depending on given [modelType] it runs

3a) For "T" -> Term based model (TdIdf)

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
	



3b) For "L" -> Language based model

	+
	+
	+


4) If judgments [qrlesPath]	are defined it will calculate 
	PrecisionRecall, AveragePrecision & MeanAveragePrecision
	with resulting scores from 3a) or 3b).





