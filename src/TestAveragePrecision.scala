import assignment.AveragePrecision

object TestAveragePrecision extends App {

  val ranked1: Seq[Int] = Seq(1, 2)
  val relevance1: Set[Int] = Set(1, 2, 3)
  val k1 = 3;

  println(new AveragePrecision(ranked1, relevance1, 3).avgPrecision)
  assert(new AveragePrecision(ranked1, relevance1, 3).avgPrecision == 0.6666667f)

  val ranked2: Seq[Int] = Seq(1, 2, 3)
  val relevance2: Set[Int] = Set(1, 3, 6)
  val k2 = 3;

  println(new AveragePrecision(ranked2, relevance2, k2).avgPrecision)
  assert(new AveragePrecision(ranked2, relevance2, k2).avgPrecision == 0.5555556f)

  val ranked3: Seq[Int] = Seq(1, 2, 3)
  val relevance3: Set[Int] = Set(1, 3, 6)
  val k3 = 2;

  println(new AveragePrecision(ranked3, relevance3, k3).avgPrecision)
  assert(new AveragePrecision(ranked3, relevance3, k3).avgPrecision == 0.5f)
  
  val ranked4: Seq[Int] = Seq(1, 3, 2)
  val relevance4: Set[Int] = Set(1, 3, 6)
  val k4 = 2;

  println(new AveragePrecision(ranked4, relevance4, k4).avgPrecision)
  assert(new AveragePrecision(ranked4, relevance4, k4).avgPrecision == 1)

}