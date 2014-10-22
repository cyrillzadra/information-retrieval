import assignment.io.MyStream
import assignment.tdidf.TdIdfIndex
import ch.ethz.dal.tinyir.io.ZipDirStream
import assignment.TipsterDirStream

object TestIndex extends App {

  //val dirPath = "C:/dev/projects/eth/information-retrieval/course-material/assignment1/zips/zips-1.2/";
  //val dirPath = "C:/dev/projects/eth/information-retrieval/course-material/assignment1/zips/zips-1-old/";
  val dirPath = "C:/dev/projects/eth/information-retrieval/course-material/assignment1/zips/all-zips/";

  val tipster: MyStream = new MyStream(dirPath);

  val t = System.currentTimeMillis()

  println(t)

  val query: Map[Int, String] = Map(51 -> "Airbus Subsidies",
    52 -> "South African Sanctions", 53 -> "Leveraged Buyouts",
    54 -> "Satellite Launch Contracts", 55 -> "Insider Trading",
    56 -> "International Finance", 57 -> "MCI",
    58 -> "Rail Strikes", 59 -> "Weather Related Fatalities",
    60 -> "Merit-Pay vs. Seniority")

  val idx: TdIdfIndex = new TdIdfIndex(tipster, query)

  println(System.currentTimeMillis() - t)
}