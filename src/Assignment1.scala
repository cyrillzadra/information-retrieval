
import assignment.ResultWriter
import ch.ethz.dal.tinyir.lectures.PrecisionRecall
import ch.ethz.dal.tinyir.lectures.TipsterGroundTruth
import ch.ethz.dal.tinyir.util.StopWatch
import assignment.MultipleAlertsTipster
import assignment.MeanAveragePrecision
import assignment.AveragePrecision
import assignment.MultipleLanguageModelAlertsTipster
import ch.ethz.dal.tinyir.indexing.SimpleIndex
import assignment.TipsterDirStream

object Assignemnet1 extends App {

  /**
   * Build a complete IR system that:
   *
   * + parses a document collection in a single-pass streaming fashion,
   */

  //val zipDirPath = "C:/dev/projects/eth/information-retrieval/course-material/assignment1/zips/";
  //val tipster: TipsterStream = new TipsterStream(zipDirPath, ".zip");
  val dirPath = "C:/dev/projects/eth/information-retrieval/course-material/assignment1/zips/zips-1.2/";
  val dirPath2 = "C:/dev/projects/eth/information-retrieval/course-material/assignment1/zips/ap880315";
  val tipster: TipsterDirStream = new TipsterDirStream(dirPath, "");

  val qrlesPath = "C:/dev/projects/eth/information-retrieval/course-material/assignment1/qrels"
  val topicPath = "C:/dev/projects/eth/information-retrieval/course-material/assignment1/topics"

  /**
   * + handles multiple queries simultaneously,
   */
  //val query : Map[Int, String] = Map(51 -> "Airbus Subsidies");
  val query: Map[Int, String] = Map(51 -> "Airbus Subsidies",
    52 -> "South African Sanctions", 53 -> "Leveraged Buyouts", 
    54 -> "Satellite Launch Contracts", 55-> "Insider Trading",
    56 -> "International Finance", 57 -> "MCI",
    58 -> "Rail Strikes", 59 -> "Weather Related Fatalities",
    60 -> "Merit-Pay vs. Seniority")
  
  val numberOfResults = 100
  //  override def ID = read(doc.getElementsByTagName("title")).hashCode()


  /**
   * +offers multiple relevance models (at least one term-based model
   * and one language model),
   */
  
  
  // tf-idf term-based model
  val multipleAlertsTipster = new MultipleAlertsTipster(query, numberOfResults)
  
  // language model model
  //val multipleAlertsTipster = new MultipleLanguageModelAlertsTipster(query, numberOfResults, 0.1)
  
  multipleAlertsTipster.process(tipster)

  /**
   * + outputs top n results per query,
   */
  new ResultWriter("ranking-cyrill-zadra.run").write(multipleAlertsTipster)

  /**
   * + calculates per-query and global quality metrics (e.g., MAP)
   */

  val ret = multipleAlertsTipster.alerts.map(r => new PrecisionRecall(r.results.map(x => x.title), new TipsterGroundTruth(qrlesPath).judgements.get(r.topic.toString).get.toSet))
  val retAvgPrev = multipleAlertsTipster.alerts.map(r => new AveragePrecision(r.results.map(x => x.title), new TipsterGroundTruth(qrlesPath).judgements.get(r.topic.toString).get.toSet, numberOfResults))

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