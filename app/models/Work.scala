package models

import java.util.UUID
import java.time.ZonedDateTime

sealed trait Work {
  val id:        UUID
  val userID:    UUID
  val topic:     String
  val startTime: Long
}
case class Current(id:        UUID,
                   userID:    UUID,
                   topic:     String,
                   startTime: Long,
                   scheduledEndTime: Long) extends Work

case class Done(id:        UUID,
                userID:    UUID,
                topic:     String,
                startTime: Long,
                endTime:   Long,
                succeeded: Boolean) extends Work
