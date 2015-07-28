package me.lightspeed7.dsug.reactive

import scala.collection.concurrent.TrieMap
import play.api.libs.json._
import nl.grons.metrics.scala._
import enumeratum.{ Enum, EnumEntry, PlayEnum }
import java.util.concurrent.TimeUnit

//
// Metric Type Enum 
// ///////////////////////////
object MetricType extends PlayEnum[MetricType] {

  case object Timer extends MetricType("timer")
  case object Counter extends MetricType("counter")

  val values = findValues

  def find(in: String): Option[MetricType] = values.filter(_.name == in).headOption
}
sealed abstract class MetricType(val name: String) extends EnumEntry {
  def getFullName(metricName: String): String = name + "_" + metricName
}

//
// Metrics Collector
// //////////////////////////////////
object MetricsCollector extends nl.grons.metrics.scala.InstrumentedBuilder {

  val metricRegistry = new com.codahale.metrics.MetricRegistry()
  val healthCheckRegistry = new com.codahale.metrics.health.HealthCheckRegistry();

  private val metricMap: TrieMap[String, Metric] = new TrieMap()

  private def register[T <: Metric](key: String, metric: T): T = {
    metricMap.put(key, metric)
    metric
  }

  def find(metricType: MetricType, metricName: String) = metricMap.get(metricType.getFullName(metricName))

  def newMetricCounter(metricName: String): MetricCounter = {
    val name = MetricType.Counter.getFullName(metricName)
    register(name, new MetricCounter(metrics.counter(name), metricName))
  }

  def newMetricTimer(metricName: String): MetricTimer = {
    val name = MetricType.Timer.getFullName(metricName)
    register(name, new MetricTimer(metrics.timer(name), metricName))
  }

  def getCurrentData(now: Long): Seq[MetricData] = metricMap.values.map { metric => metric.getCurrentData(now) }.toSeq

}

//
// Metric Data Holding 
// //////////////////////////////
case class MetricData(timestamp: Long, metricType: MetricType, baseName: String, metricName: String, value: Long)
object MetricData {
  implicit val format = Json.format[MetricData]
}

//
// Base trait for all  metrics classes
// /////////////////////////////////////////
sealed trait Metric {
  private[reactive] def metricType: MetricType

  private[reactive] def toMillis(in: Long): Long = in / (1000 * 1000)

  def getCurrentData(now: Long): MetricData
}

class MetricCounter(counter: Counter, baseName: String) extends Metric {

  private[reactive] val metricType = MetricType.Counter

  // Not exposing everything on purpose.
  def increment = incrementBy(1)
  def decrement = decrementBy(1)

  def count: Long = counter.count

  def incrementBy(delta: Long) = counter.inc(delta)
  def decrementBy(delta: Long) = counter.dec(delta)

  def getCurrentData(now: Long): MetricData = {
    val currentValue = counter.count
    decrementBy(currentValue) // reset back to "zero" for next pull
    MetricData(now, metricType, baseName, metricType.name, currentValue)
  }
}

class MetricTimer(timer: Timer, baseName: String) extends Metric {

  private[reactive] val metricType = MetricType.Timer

  // Not exposing everything on purpose.
  def time[A](f: => A): A = timer.time(f)
  def update(durationInMillis: Long) = timer.update(durationInMillis, TimeUnit.MILLISECONDS)

  def count: Long = timer.count

  def getCurrentData(now: Long): MetricData = {
    val currentValue = timer.snapshot.get95thPercentile() // try this one for now.
    MetricData(now, metricType, baseName, metricType.name, toMillis(currentValue.toLong))
  }
}
