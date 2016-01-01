package models

import java.util.UUID
import java.time.ZonedDateTime

sealed trait Work {
  val id:        UUID
  val userID:    UUID
  val topic:     String
  val startTime: ZonedDateTime
}
case class Current(id:        UUID,
                   userID:    UUID,
                   topic:     String,
                   startTime: ZonedDateTime) extends Work

case class Done(id:        UUID,
                userID:    UUID,
                topic:     String,
                startTime: ZonedDateTime,
                endTime:   ZonedDateTime,
                succeeded: Boolean) extends Work
