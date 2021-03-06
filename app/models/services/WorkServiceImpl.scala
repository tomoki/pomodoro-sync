package models.services

import java.util.UUID
import java.time.ZonedDateTime

import models.{Current, Done}
import scala.concurrent.Future
import javax.inject.Inject
import models.daos.WorkDAO

class WorkServiceImpl @Inject() (workDAO: WorkDAO) extends WorkService {
  def getCurrent(userID: UUID) : Future[Option[Current]] =
    workDAO.getCurrent(userID)
  def updateCurrent(id: UUID, userID: UUID, topic: String, startTime: Long, scheduledEndTime: Long) : Future[Current] =
    workDAO.updateCurrent(id, userID, topic, startTime, scheduledEndTime)
  def markCurrent(userID: UUID, succeeded: Boolean, when: Long) : Future[Option[Done]] =
    workDAO.markCurrent(userID, succeeded, when)
  def getPastWorks(userID: UUID, when: Long) : Future[List[Done]] =
    workDAO.getPastWorks(userID, when)
}
