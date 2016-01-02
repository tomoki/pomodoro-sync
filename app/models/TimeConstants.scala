package models

// in [ms]
trait TimeConstants {
  def workTime() : Long
  def intervalBetweenWorks() : Long
  def bigIntervalBetweenWorks() : Long
}
