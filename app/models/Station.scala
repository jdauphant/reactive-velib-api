package models

case class Station(number: Int,
                   contract_name: String,
                  name: String,
                  address: String,
                  position: Position,
                  banking: Boolean,
                  bonus: Boolean,
                  status: String,
                  bike_stands: Int,
                  available_bike_stands: Int,
                  available_bikes: Int,
                  last_update: Double)
