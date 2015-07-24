package controllers

import actors.{StationsDBActor, WebSocketActor}
import models._
import play.Logger
import play.api.Play
import play.api.libs.json._
import play.api.libs.ws._
import play.api.mvc._
import akka.actor._
import akka.pattern.ask
import javax.inject._

import StationsDBActor._

import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits.defaultContext

@Singleton
class Application @Inject() (system: ActorSystem, ws: WSClient) extends Controller {
  val contract = Play.configuration.getString("jcdecaux.api.contract").get
  val apiKey = Play.configuration.getString("jcdecaux.api.key").get

  implicit val positionReads = Json.reads[Position]
  implicit val stationReads = Json.reads[Station]

  val stationsDBActor = system.actorOf(StationsDBActor.props, "stations-db-actor")

  def socket = WebSocket.acceptWithActor[JsValue, JsValue] { request => out =>
    WebSocketActor.props(stationsDBActor,out)
  }

  def update = Action.async {
    ws.url(s"https://api.jcdecaux.com/vls/v1/stations?contract=$contract&apiKey=$apiKey").get().map {
      _.json.validate[List[Station]].fold(
      validationErrors => {
        Logger.error(validationErrors.toString())
        InternalServerError
      }, {
        case stations =>
          stations.map {
            stationsDBActor ! Upsert(_)
          }
          Ok
      })
    }

  }
}