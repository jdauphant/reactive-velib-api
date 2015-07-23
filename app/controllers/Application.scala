package controllers

import actors.WebSocketActor
import play.api.mvc._
import akka.actor._
import javax.inject._

import play.api.Play.current

@Singleton
class Application @Inject() (system: ActorSystem) extends Controller {

  def socket = WebSocket.acceptWithActor[String, String] { request => out =>
    WebSocketActor.props(out)
  }
}