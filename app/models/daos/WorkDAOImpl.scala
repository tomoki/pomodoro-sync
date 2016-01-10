package models.daos

import java.util.UUID
import java.time.ZonedDateTime
import models.Current
import models.Done
import models.User


import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider

class WorkDAOImpl @Inject()(
  protected val dbConfigProvider: DatabaseConfigProvider) extends WorkDAO with DAOSlick {
  import driver.api._
  def getCurrent(userID: UUID) : Future[Option[Current]] = {
    val query = for {
      cur <- slickCurrents.filter(_.userID === userID.toString)
    } yield cur
    db.run(query.result.headOption).map { dbCurrentOption =>
      dbCurrentOption.map { dbCurrent =>
        Current(UUID.fromString(dbCurrent.id), UUID.fromString(dbCurrent.userID),
                dbCurrent.topic, dbCurrent.startTime, dbCurrent.scheduledEndTime)
      }
    }
  }
  def updateCurrent(id: UUID, userID: UUID, topic: String, startTime: Long, scheduledEndTime: Long) : Future[Current] = {
    val newDBCurrent = DBCurrent(id.toString, userID.toString, topic, startTime, scheduledEndTime)
    val query = slickCurrents += (newDBCurrent)
    db.run(query).flatMap { _ => getCurrent(userID).map(_.get)}
  }

  def markCurrent(userID: UUID, succeeded: Boolean, when: Long) : Future[Option[Done]] = {
    getCurrent(userID).flatMap {
      case Some(current) => {
        val done = Done(current.id, current.userID, current.topic,
                        current.startTime, when, succeeded)
        val dbdone = DBDone(done.id.toString, done.userID.toString, done.topic,
                            done.startTime, done.endTime, done.succeeded)
        val query1 = slickDones += dbdone
        val query2 = slickCurrents.filter(_.userID === userID.toString).delete
        db.run(query1).flatMap(_ => db.run(query2)).map(_ => Some(done))
      }
      case None => Future.successful(None)
    }
  }
  def getPastWorks(userID: UUID, when: Long) : Future[List[Done]] = {
    val query = slickDones.filter(_.userID === userID.toString).sortBy(_.startTime)
    db.run(query.result).map(dbdonelist =>
      dbdonelist.map(dbdone =>
        Done(UUID.fromString(dbdone.id),
             UUID.fromString(dbdone.userID),
             dbdone.topic,
             dbdone.startTime,
             dbdone.endTime,
             dbdone.succeeded)).toList)
    }
}
