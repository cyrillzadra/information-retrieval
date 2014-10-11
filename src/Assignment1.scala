
import assignment.ResultWriter
import assignment.Tipster3Stream
import ch.ethz.dal.tinyir.lectures.PrecisionRecall
import ch.ethz.dal.tinyir.lectures.TipsterGroundTruth
import ch.ethz.dal.tinyir.util.StopWatch
import assignment.MultipleAlertsTipster
import assignment.MeanAveragePrecision
import assignment.AveragePrecision

object Assignemnet1 extends App {

  /**
   * Build a complete IR system that:
   *
   * + parses a document collection in a single-pass streaming fashion,
   */

  //val zipDirPath = "C:/dev/projects/eth/information-retrieval/course-material/assignment1/zips/";
  //val tipster: TipsterStream = new TipsterStream(zipDirPath, ".zip");
  val dirPath = "C:/dev/projects/eth/information-retrieval/course-material/assignment1/zips/zips-1/";
  val dirPath2 = "C:/dev/projects/eth/information-retrieval/course-material/assignment1/zips/ap880315";
  val tipster: Tipster3Stream = new Tipster3Stream(dirPath, "");

  val qrlesPath = "C:/dev/projects/eth/information-retrieval/course-material/assignment1/qrels"
  val topicPath = "C:/dev/projects/eth/information-retrieval/course-material/assignment1/topics"

  /**
   * + handles multiple queries simultaneously,
   */
  //val query: Map[Int, String] = Map(51 -> "Airbus Subsidies", 52 -> "South African Sanctions", 53 -> "Leveraged Buyouts")
    val query: Map[Int, String] = Map(51 -> "Airbus Subsidies")
  val num = 100

  /**
   * +offers multiple relevance models (at least one term-based model
   * and one language model),
   */

  val alerts = new MultipleAlertsTipster(query, num)

  val sw = new StopWatch; sw.start
  var iter = 0
  for (doc <- tipster.stream) {
    iter += 1
    alerts.process(doc.name, doc.tokens)
    if (iter % 20000 == 0) {
      println("Iteration = " + iter)
      alerts.results.foreach(println)
    }
  }
  sw.stop
  println("Stopped time = " + sw.stopped)
  println("########## RESULT ##########")

  /**
   * + outputs top n results per query,
   */
  new ResultWriter("ranking-cyrill-zadra.run").write(alerts)

  /**
   * + calculates per-query and global quality metrics (e.g., MAP)
   */

  val rel = new TipsterGroundTruth(qrlesPath).judgements.get("51").get.toSet
  println("########## RELEVANCE ##########")
  println(rel)

  val ret = alerts.results.map(r => new PrecisionRecall(r.map(x => x.title), rel))
  val retAvgPrev = alerts.results.map(r => new AveragePrecision(r.map(x => x.title), rel, num))

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