package assignment

import ch.ethz.dal.tinyir.alerts.ScoredResult
import ch.ethz.dal.tinyir.alerts.Alerts

abstract class AbstractTipster {

  def process(): Unit

  def results : List[List[ScoredResult]]
  
  def alerts : List[Alerts]

}