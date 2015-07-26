package actors

import actors.StationActor.{ValueChanged, GetResult}
import actors.StationsDBActor.{GetAll, SubscribeAll, UnknownStation, GetByNumber}
import akka.actor._
import akka.event.LoggingReceive
import models.{Position, Station}
import play.api.libs.json._

object WebSocketActor {
  def props(stationsDB: ActorRef, out: ActorRef) = Props(new WebSocketActor(stationsDB,out))
}

class WebSocketActor(stationsDB: ActorRef, out: ActorRef) extends Actor {
  implicit val positionWrites = Json.writes[Position]
  implicit val stationWrites = Json.writes[Station]

  def receive = LoggingReceive {
    case request: JsArray =>
      (request(0).toOption,request(1).toOption) match {
        case (Some(JsString("get")),Some(JsNumber(number))) =>
          stationsDB ! GetByNumber(number.toIntExact)
        case (Some(JsString("subscribeAll")),None) =>
          stationsDB ! SubscribeAll
        case (Some(JsString("getAll")),None) =>
          stationsDB ! GetAll
        case _ =>
          error(s"unknown command")
      }
    case GetResult(station: Station) =>
      out ! Json.arr(JsString("station"), Json.toJson(station))
    case UnknownStation(number) =>
      error(s"unknown station $number")
    case ValueChanged(number, key, oldValue, newValue) =>
      out ! Json.arr(JsString("update"),
        Json.obj(
          "number" -> number,
          "key" -> key,
          "oldValue" -> oldValue,
          "newValue" -> newValue
        )
      )
  }

  def error(message: String) = out ! Json.arr(JsString("error"), Json.obj(
    "message" -> JsString(message)
  ))
}