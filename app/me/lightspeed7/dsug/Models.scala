package me.lightspeed7.dsug

import play.api.libs.json._

// to send to the statistics actor
case class Counts(key: String, value: Long)
//
// Payload object sent to Angular
// //////////////////////////////
case class Payload(data: JsValue, target: String)
object Payload {
  implicit val payloadFormat = Json.format[Payload]
}

case class Count(key: String, count: Long) {
  def inc(v: Long) = copy(count = count + v)
}
object Count {
  implicit val countFormat = Json.format[Count]
}

case class TimeSeriesCount(ts: Long, data: Seq[Count])
object TimeSeriesCount {
  implicit val timeSeriesFormat = Json.format[TimeSeriesCount]
}
