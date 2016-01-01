package models.services

import java.util.UUID
import java.time.ZonedDateTime

import models.Current
import scala.concurrent.Future
import javax.inject.Inject
import models.daos.WorkDAO

class WorkServiceImpl @Inject() (workDAO: WorkDAO) extends WorkService {
  def getCurrent(userID: UUID) : Future[Option[Current]] =
    workDAO.getCurrent(userID)
  def updateCurrent(id: UUID, userID: UUID, topic: String, startTime: ZonedDateTime) : Future[Current] =
    workDAO.updateCurrent(id, userID, topic, startTime)
}
