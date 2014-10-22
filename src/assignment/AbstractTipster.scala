package assignment

import ch.ethz.dal.tinyir.alerts.Alerts
import ch.ethz.dal.tinyir.alerts.ScoredResult
import java.io.File

abstract class AbstractTipster {

  def process(): Unit

  def results: List[List[ScoredResult]]

  def alerts: List[Alerts]

}