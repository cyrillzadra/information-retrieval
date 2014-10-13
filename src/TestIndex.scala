import assignment.TipsterDirStream
import ch.ethz.dal.tinyir.indexing.SimpleIndex

object TestIndex extends App {

  val dirPath = "C:/dev/projects/eth/information-retrieval/course-material/assignment1/zips/zips-1/";

  val tipster: TipsterDirStream = new TipsterDirStream(dirPath, "");

  val t = System.currentTimeMillis()

  println ( t )

  //val idx: FreqIndex = new FreqIndex(tipster.stream)
  val idx: SimpleIndex = new SimpleIndex(tipster.stream)

  println(System.currentTimeMillis() - t)

  println(idx.index)

}