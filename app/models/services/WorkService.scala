package models.services

import models.User
import models.Work
import models.Current
import models.Done
import java.util.UUID
import java.time.ZonedDateTime

import scala.concurrent.Future

trait WorkService {
  def getCurrent(userID: UUID): Future[Option[Current]]
  def updateCurrent(id: UUID, userID: UUID, topic: String, startTime: Long, scheduledEndTime: Long) : Future[Current]
  def markCurrent(userID: UUID, succeeded: Boolean, when: Long) : Future[Option[Done]]
}
