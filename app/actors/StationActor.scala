package actors

import akka.actor._
import models.Station

object StationActor {
  def props(station: Station) = Props(new StationActor(station))

  case class Update(station: Station)
  case class Get(requester: ActorRef)
  case class GetResult(station: Station)
  case class Subscribe(observer: ActorRef)
  case class UnSubscribe(observer: ActorRef)
  case class ValueChanged(number: Int, key: String, oldValue: Int, newValue: Int)
}

class StationActor(station: Station) extends Actor {
  import actors.StationActor._

  var observers = Set[ActorRef]()

  def receive = data(station)

  def data(station: Station): Receive = {
    case Update(updatedStation) =>
      // check and notified difference between station and updatedStation
      if(updatedStation.available_bikes!=station.available_bikes) {
        notify("available_bikes",station.available_bikes, updatedStation.available_bikes)
      }
      if(updatedStation.available_bike_stands!=station.available_bike_stands) {
        notify("available_bike_stands",station.available_bike_stands, updatedStation.available_bike_stands)
      }
      if(updatedStation.bike_stands!=station.bike_stands) {
        notify("bike_stands",station.bike_stands, updatedStation.bike_stands)
      }
      context.become(data(updatedStation))
    case Get(requester: ActorRef) =>
      requester ! GetResult(station)
    case Subscribe(observer) =>
      observers += observer
    case UnSubscribe(observer) =>
      observers -= observer
  }

  def notify(key: String, oldValue: Int, newValue: Int) = observers.foreach {
    _ ! ValueChanged(station.number,key,oldValue,newValue)
  }
}