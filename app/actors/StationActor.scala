package actors

import akka.actor._
import models.Station

object StationActor {
  def props(station: Station) = Props(new StationActor(station))

  case class Update(station: Station)
  case class Get(sender: ActorRef)
  case class GetResult(station: Station)
}

class StationActor(station: Station) extends Actor {
  import actors.StationActor._

  def receive = data(station)

  def data(station: Station): Receive = {
    case Update(updatedStation) =>
      // check and notified difference between station and updatedStation
      context.become(data(updatedStation))
    case Get(sender: ActorRef) =>
      sender ! GetResult(station)
  }
}