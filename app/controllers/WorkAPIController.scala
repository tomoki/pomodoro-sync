package controllers


import javax.inject.Inject
import java.util.UUID
import java.time.ZonedDateTime

import com.mohiva.play.silhouette.api.{ Environment, LogoutEvent, Silhouette }
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry


import models.User
import models.{Work, Current, Done}
import models.TimeConstants
import models.services.WorkService

import forms._
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsNull,Json,JsString,JsValue}
import play.api.mvc.Result
import play.api.mvc.RequestHeader
import play.api.Application
import play.Logger;
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.concurrent.Akka
import akka.actor.ActorSystem

import scala.concurrent.Future
import scala.concurrent.duration.{Duration, MILLISECONDS}

class WorkAPIController @Inject() (
  val messagesApi: MessagesApi,
  val env: Environment[User, CookieAuthenticator],
  system: ActorSystem,
  workService: WorkService,
  timeConstants: TimeConstants,
  socialProviderRegistry: SocialProviderRegistry)
    extends Silhouette[User, CookieAuthenticator] {

  override def onNotAuthenticated(request: RequestHeader): Option[Future[Result]] = {
    val json = errorJSON("Not authenticated")
    Some(Future.successful(Ok(json.toString)))
  }
  def errorJSON(error_message: String,
                info: JsValue = Json.obj()): JsValue =
    Json.obj(
      "error" -> error_message,
      "info"  -> info
    )
  def toJSON(work: Work) : JsValue =
    work match {
      case Current(id, userID, topic, startTime, scheduledEndTime) =>
        Json.obj(
          "id"        -> id.toString,
          "topic"     -> topic,
          "startTime" -> startTime,
          "scheduledEndTime" -> scheduledEndTime
        )
      case Done(id, userID, topic, startTime, endTime, succeeded) =>
        Json.obj(
          "id"        -> id.toString,
          "topic"     -> topic,
          "startTime" -> startTime,
          "endTime"   -> endTime,
          "succeeded" -> succeeded
        )
    }

  def current = SecuredAction.async {
    implicit request =>
    workService.getCurrent(request.identity.userID).flatMap {
      case Some(current) => {
        Future.successful(Ok(toJSON(current).toString))
      }
      case None => {
        Future.successful(Ok(JsNull.toString))
      }
    }
  }
  def updateCurrent = SecuredAction.async {
    implicit request =>
    workService.getCurrent(request.identity.userID).flatMap {
      case Some(current) => {
        val json = errorJSON("Already have work", toJSON(current))
        Future.successful(Ok(json.toString))
      }
      case None => {
        val data   = request.body.asFormUrlEncoded
        data match {
          case Some(data_map) if data_map.contains("topic") => {
            val id               = UUID.randomUUID()
            val userID           = request.identity.userID
            val topic            = data_map("topic").mkString("") // ????
            val startTime        = System.currentTimeMillis
            val scheduledEndTime = startTime + timeConstants.workTime
            import play.api.libs.concurrent.Execution.Implicits.defaultContext
            import scala.concurrent.duration._
            system.scheduler.scheduleOnce((scheduledEndTime - startTime) milliseconds) {
              import scala.concurrent._
              import ExecutionContext.Implicits.global
              blocking {
                workService.markCurrent(userID, true, scheduledEndTime)
              }
            }
            workService.updateCurrent(id, userID, topic, startTime, scheduledEndTime).flatMap(current =>
              Future.successful(Ok(toJSON(current).toString)))
          }
          case None => {
            val json = errorJSON("Invalid request. Maybe topic is not here?")
            Future.successful(Ok(json.toString))
          }
        }
      }
    }
  }
  def giveupCurrent = SecuredAction.async {
    implicit request =>
    val userID = request.identity.userID
    val when   = System.currentTimeMillis
    workService.markCurrent(userID, false, when).map {
      case Some(done) => Ok(toJSON(done).toString)
      case None       => Ok(errorJSON("There is no current job.").toString)
    }
  }
}
