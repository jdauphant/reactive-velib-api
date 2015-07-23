package actors

import akka.actor._
import play.api.libs.json._

object WebSocketActor {
  def props(out: ActorRef) = Props(new WebSocketActor(out))
}

class WebSocketActor(out: ActorRef) extends Actor {
  def receive = {
    case msg: JsValue =>
      out ! Json.toJson(Map("received" -> msg))
  }
}