package models.daos

import java.util.UUID
import java.time.ZonedDateTime
import models.Current
import models.Done
import scala.concurrent.Future

trait WorkDAO {
  def getCurrent(userID: UUID) : Future[Option[Current]]
  def updateCurrent(id: UUID, userID: UUID, topic: String, startTime: Long, scheduledEndTime: Long) : Future[Current]
  def markCurrent(userID: UUID, succeeded: Boolean, when: Long) : Future[Option[Done]]
  def getPastWorks(userID: UUID, when: Long) : Future[List[Done]]
}
