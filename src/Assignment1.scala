
import assignment.TipsterDirStream
import assignment.langmodel.LanguageModelAlertsTipster
import assignment.AveragePrecision
import ch.ethz.dal.tinyir.lectures.TipsterGroundTruth
import ch.ethz.dal.tinyir.lectures.PrecisionRecall
import assignment.MeanAveragePrecision

object Assignemnet1 extends App {

  /**
   * Build a complete IR system that:
   *
   * + parses a document collection in a single-pass streaming fashion,
   */

  //val tipster: TipsterStream = new TipsterStream(zipDirPath, ".zip");
  val dirPath = "C:/dev/projects/eth/information-retrieval/course-material/assignment1/zips/zips-1-old/";
  val dirPath3 = "C:/dev/projects/eth/information-retrieval/course-material/assignment1/zips/zips-1.2/";
  val dirPath2 = "C:/dev/projects/eth/information-retrieval/course-material/assignment1/zips/all-zips/";
  val tipster: TipsterDirStream = new TipsterDirStream(dirPath, "");

  val qrlesPath = "C:/dev/projects/eth/information-retrieval/course-material/assignment1/qrels"
  val topicPath = "C:/dev/projects/eth/information-retrieval/course-material/assignment1/topics"

  //val query: Map[Int, String] = Map(51 -> "Airbus Subsidies");
   val query: Map[Int, String] = Map(51 -> "Airbus Subsidies",
    52 -> "South African Sanctions", 53 -> "Leveraged Buyouts",
    54 -> "Satellite Launch Contracts", 55 -> "Insider Trading",
    56 -> "International Finance", 57 -> "MCI",
    58 -> "Rail Strikes", 59 -> "Weather Related Fatalities",
    60 -> "Merit-Pay vs. Seniority")

  //  val query: Map[Int, String] = Map(91 -> "U.S. Army Acquisition of Advanced Weapons Systems",
  //    92 -> "International Military Equipment Sales", 93 -> "What Backing Does the National Rifle Association Have?",
  //    94 -> "Computer-aided Crime", 95 -> "Computer-aided Crime Detection",
  //    96 -> "Computer-Aided Medical Diagnosis", 97 -> "Fiber Optics Applications",
  //    98 -> "Fiber Optics Equipment Manufacturers", 99 -> "Iran-Contra Affair",
  //    100 -> "Controlling the Transfer of High Technology")

  val numberOfResults = 100
  
  println("model...  ")

  // tf-idf term-based model
  //val multipleAlertsTipster = new TermBasedModelAlertsTipster(query, numberOfResults, tipster)

  //language model model
  val multipleAlertsTipster = new LanguageModelAlertsTipster(query, numberOfResults, tipster, 0.1)

  multipleAlertsTipster.process()


  /**
   * + calculates per-query and global quality metrics (e.g., MAP)
   */
  
  println("calculating ... ")
  
  val ret = multipleAlertsTipster.alerts.map(r =>
    new PrecisionRecall(r.results.map(x => x.title),
      new TipsterGroundTruth(qrlesPath).judgements.get(r.topic.toString).get.toSet))
      
  val retAvgPrev = multipleAlertsTipster.alerts.map(r =>
    new AveragePrecision(r.results.map(x => x.title),
      new TipsterGroundTruth(qrlesPath).judgements.get(r.topic.toString).get.toSet, numberOfResults))

  for (pr <- ret) {
    println("########## PrecisionRecall ###########")
    println(List(pr.relevIdx.mkString, pr.precs.mkString(";"), pr.iprecs.mkString(";")).mkString(", "))
  }

  for (pr <- retAvgPrev) {
    println("########## AveragePrecision ###########")
    println(pr.avgPrecision)
  }

  println("########## MeanAveragePrecision ###########")

  println(new MeanAveragePrecision(retAvgPrev, query.size).meanAvgPrecision)

}