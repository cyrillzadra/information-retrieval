import assignment.TipsterDirStream
import ch.ethz.dal.tinyir.processing.Document
import assignment.tdidf.TdIdfIndex

object TestIndex extends App {

  //val dirPath = "C:/dev/projects/eth/information-retrieval/course-material/assignment1/zips/zips-1.2/";
  val dirPath = "C:/dev/projects/eth/information-retrieval/course-material/assignment1/zips/zips-1-old/";
  //val dirPath = "C:/dev/projects/eth/information-retrieval/course-material/assignment1/zips/all-zips/";

  val tipster: TipsterDirStream = new TipsterDirStream(dirPath, "");

  val t = System.currentTimeMillis()

  println(t)

  val query: Map[Int, String] = Map(51 -> "Airbus Subsidies",
    52 -> "South African Sanctions", 53 -> "Leveraged Buyouts",
    54 -> "Satellite Launch Contracts", 55 -> "Insider Trading",
    56 -> "International Finance", 57 -> "MCI",
    58 -> "Rail Strikes", 59 -> "Weather Related Fatalities",
    60 -> "Merit-Pay vs. Seniority")

  val idx: TdIdfIndex = new TdIdfIndex(tipster.stream, query)

  println(System.currentTimeMillis() - t)

  println("numberOfDocuments" + idx.numberOfDocuments)
  println("numberOfDocmentsByTerm " + idx.numberOfDocmentsByTerm)
  println("filteredNumberOfDocmentsByTerm " + idx.filteredNumberOfDocmentsByTerm)
  println("idf " + idx.idf)

  println(idx.numberOfDocmentsByTerm.take(10))
  println(idx.numberOfDocmentsByTerm.size)

  println(idx.numberOfDocuments)

}