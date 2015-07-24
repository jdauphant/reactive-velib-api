package actors

import akka.actor._
import akka.event.LoggingReceive
import models.Station

object StationsDBActor {
  def props = Props[StationsDBActor]

  case class GetByNumber(number: Int)
  case class Upsert(station: Station)
  case class UnknownStation(number: Int)
}

class StationsDBActor extends Actor {
  import StationsDBActor._
  import StationActor._

  var stations = Map[Int,ActorRef]()

  def createStationActor(station: Station) = context.actorOf(StationActor.props(station),s"s${station.number}")

  def receive = LoggingReceive {
    case Upsert(station) =>
      stations.get(station.number) match {
        case Some(actorRef) =>
          actorRef ! Update(station)
        case None =>
          stations += station.number -> createStationActor(station)
      }

    case GetByNumber(number) =>
      stations.get(number) match {
        case Some(actorRef) =>
          actorRef ! Get(sender())
        case None =>
          sender() ! UnknownStation(number)
      }
  }
}