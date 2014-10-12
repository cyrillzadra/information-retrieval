package assignment

class MeanAveragePrecision[A](avgP : List[AveragePrecision[A]], Q : Int) {

  def meanAvgPrecision() = {
    avgP.map( x => x.avgPrecision ).sum / Q
  }
}