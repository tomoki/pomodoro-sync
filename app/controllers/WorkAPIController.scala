package controllers


import javax.inject.Inject
import java.util.UUID
import java.time.ZonedDateTime

import com.mohiva.play.silhouette.api.{ Environment, LogoutEvent, Silhouette }
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import scala.concurrent.ExecutionContext.Implicits.global

import forms._
import models.User
import models.{Work, Current, Done}
import models.services.WorkService
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsNull,Json,JsString,JsValue}
import play.api.mvc.Result
import play.api.mvc.RequestHeader

import scala.concurrent.Future


class WorkAPIController @Inject() (
  val messagesApi: MessagesApi,
  val env: Environment[User, CookieAuthenticator],
  workService: WorkService,
  socialProviderRegistry: SocialProviderRegistry)
    extends Silhouette[User, CookieAuthenticator] {

  override def onNotAuthenticated(request: RequestHeader): Option[Future[Result]] = {
    val json: JsValue = Json.obj(
      "error" -> "Not authenticated",
      "info"  -> Json.obj()
    )
    Some(Future.successful(Ok(json.toString)))
  }
  def toJSON(work: Work) : JsValue =
    work match {
      case Current(id, userID, topic, startTime) =>
        Json.obj(
          "id"        -> id.toString,
          "topic"     -> topic,
          "startTime" -> startTime.toEpochSecond()
        )
      case Done(id, userID, topic, startTime, endTime, succeeded) =>
        Json.obj(
          "id"        -> id.toString,
          "topic"     -> topic,
          "startTime" -> startTime.toEpochSecond(),
          "endTime"   -> endTime.toEpochSecond(),
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
        val json: JsValue = Json.obj(
          "error" -> "Already have work",
          "info"  -> toJSON(current)
        )
        Future.successful(Ok(json.toString))
      }
      case None => {
        val data   = request.body.asFormUrlEncoded
        data match {
          case Some(data_map) if data_map.contains("topic") => {
            val id        = UUID.randomUUID()
            val userID    = request.identity.userID
            val topic     = data_map("topic").mkString("") // ????
            val startTime = ZonedDateTime.now()

            workService.updateCurrent(id, userID, topic, startTime).flatMap(current =>
              Future.successful(Ok(toJSON(current).toString)))
          }
          case None => {
            val json: JsValue = Json.obj(
              "error" -> "Invalid request. Maybe topic is not here?",
              "info"  -> Json.obj()
            )
            Future.successful(Ok(json.toString))
          }
        }
      }
    }
  }
}
