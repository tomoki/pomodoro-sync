package models.daos

import java.util.UUID
import java.time.ZonedDateTime
import models.Current
import scala.concurrent.Future

trait WorkDAO {
  def getCurrent(userID: UUID) : Future[Option[Current]]
  def updateCurrent(id: UUID, userID: UUID, topic: String, startTime: ZonedDateTime) : Future[Current]
}
