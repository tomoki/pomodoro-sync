package models.daos

import java.util.UUID
import java.time.ZonedDateTime
import models.Current
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
                dbCurrent.topic, ZonedDateTime.parse(dbCurrent.startTime))
      }
    }
  }
  def updateCurrent(id: UUID, userID: UUID, topic: String, startTime: ZonedDateTime) : Future[Current] = {
    val newDBCurrent = DBCurrent(id.toString, userID.toString, topic, startTime.toString)
    val query = slickCurrents += (newDBCurrent)
    db.run(query).flatMap { _ => getCurrent(userID).map(_.get)}
  }
}
