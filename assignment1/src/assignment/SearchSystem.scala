
package assignment

import assignment.tdidf.TdIdfAlertsTipster
import ch.ethz.dal.tinyir.lectures.PrecisionRecall
import ch.ethz.dal.tinyir.lectures.TipsterGroundTruth
import assignment.langmodel.LangModelAlertsTipster
import assignment.io.MyStream
import scala.io.Source

object SearchSystem extends App {

  val defaultInputPath1 = "C:/dev/projects/eth/information-retrieval/course-material/assignment1/zips/zips-1-old/";
  val defaultInputPath2 = "C:/dev/projects/eth/information-retrieval/course-material/assignment1/zips/zips-1.2/";
  val defaultInputPathAll = "C:/dev/projects/eth/information-retrieval/course-material/assignment1/zips/all-zips/";

  val defaultQrlesPath = "C:/dev/projects/eth/information-retrieval/course-material/assignment1/qrels"

  val defaultModelType = "T"

  /**
   *  T for term based model
   *  L for language based model
   */
  val modelType: String = { if (args.length > 0) args(0) else throw new Exception("no model type defined") }
  val inputPath: String = { if (args.length > 1) args(1) else throw new Exception("no input path defined") }
  val queryPath: String = { if (args.length > 2) args(2) else throw new Exception("no query path defined") }
  val qrlesPath: String = { if (args.length > 3) args(3) else null } //optional

  println("START")
  println("input path = " + inputPath)

  val tgt: TipsterGroundTruth = { if (qrlesPath != null) new TipsterGroundTruth(qrlesPath) else null }
  if (tgt != null) {
    println("qrles path = " + qrlesPath)
  } else {
    println("no qrles (judgment) file defined")
  }

  val queries: Map[Int, String] =
    Source.fromFile(queryPath).getLines().map(l => l.split(":")).map(e => e(0).toInt -> e(1)).toMap

  println("Queries ->")
  println(queries)

  //val tipster: TipsterDirStream = new TipsterDirStream(inputPath, "");
  val tipster: MyStream = new MyStream(inputPath);

  val numberOfResults = 100

  // tf-idf term-based model
  val multipleAlertsTipster: AbstractTipster = {
    if (modelType == "T")
      new TdIdfAlertsTipster(queries, numberOfResults, tipster)
    else if (modelType == "L")
      new LangModelAlertsTipster(queries, numberOfResults, tipster, 0.1)
    else
      throw new Exception("modelType " + modelType + " is unknown")
  }

  multipleAlertsTipster.process()

  if (tgt != null) {

    println("Calculating Query Results... ")

    val queryResults = multipleAlertsTipster.alerts.filter(r => tgt.judgements.get(r.topic.toString) != None ).map(r =>
      new QueryResultCalculator(r.results.map(x => x.title), tgt.judgements.get(r.topic.toString).get.toSet, 
          	numberOfResults, r.topic, r.queryString))

    for (res <- queryResults) {
      println("########## QueryResult Topic: " + res.topic + " Query -> " + res.query)
      println("Precision values = " + res.precisionRecall.precs.mkString(" ,"))
      println("Interpolation of precision = " + res.precisionRecall.iprecs.mkString(" ,"))
      println("AvgPrecision = " + res.avgPrecision.avgPrecision)
    }

    println("###########################################")
    println("########## MeanAveragePrecision ###########")
    println("###########################################")

    println(new MeanAveragePrecision(queryResults.map(x => x.avgPrecision ), queries.size).meanAvgPrecision)

  } else {

  }

}