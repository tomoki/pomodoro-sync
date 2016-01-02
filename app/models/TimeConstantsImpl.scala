package models

import scala.concurrent.duration._
import scala.language.postfixOps
class TimeConstantsImpl extends TimeConstants {
  def workTime()                = (25 minutes).toMillis
  def intervalBetweenWorks()    = (5  minutes).toMillis
  def bigIntervalBetweenWorks() = (20 minutes).toMillis
}
