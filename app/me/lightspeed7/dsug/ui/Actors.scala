package me.lightspeed7.dsug.ui

import scala.collection.JavaConversions.asScalaIterator
import scala.util.Random

import akka.actor._
import akka.event._
import me.lightspeed7.dsug._
import play.api.Logger
import play.api.libs.iteratee.Concurrent
import play.api.libs.json._
import play.api.libs.json.JsNumber

object `package` {

  import play.api.libs.concurrent.Execution.Implicits.defaultContext

  //
  // Internal Actor State Classes
  // ///////////////////////////////
  case class Start(out: Concurrent.Channel[JsValue])
  case class MessageEvent(channel: String, payload: Payload)

  //
  // Message Bus
  // ////////////////////////////////
  private val EventBus = new LookupEventBus

  class LookupEventBus extends ActorEventBus with LookupClassification {
    type Event = MessageEvent
    type Classifier = String

    protected def mapSize(): Int = 4

    protected def classify(event: Event): Classifier = {
      event.channel
    }

    protected def publish(event: Event, subscriber: ActorRef): Unit = {
      subscriber ! event.payload
    }
  }

  //
  // Socket Listeners - ephemeral ( session )
  // ///////////////////////////////
  class Listener(name: String, out: Concurrent.Channel[JsValue]) extends Actor {

    def receive = {
      case p: Payload => {
        Logger.debug(s"Payload to client ${p}")
        val fo = Json.toJson(p)
        out.push(fo) // Pushing messages to Channel
      }
    }

    override def preStart() {
      super.preStart()
      EventBus.subscribe(self, "payload")
      Logger.info(s"Listener Ready - ${name}, path - ${self.path}")

    }

    override def postStop() {

      EventBus.unsubscribe(self, "payload")
      super.postStop()
      Logger.info(s"Listener ShutDown - ${name}, path - ${self.path}")
    }
  }

  //
  // Statistics Actor
  // /////////////////////////////////////
  class StatisticsActor(name: String) extends Actor {
    import me.lightspeed7.dsug.reactive._

    def receive = {
      case Counts(key, value) => {
        EventBus.publish(MessageEvent("payload", Payload(JsNumber(value), key)))

        val now = System.currentTimeMillis() / 1000
        
        val results = Seq("CO", "NY", "FL", "CA", "HI").map { st => Count(st, Random.nextInt(100)) }
        val metrics = MetricsCollector.getCurrentData(now)
          .filter(_.metricType == MetricType.Counter)
          .map { m => Count(m.baseName, m.value) }

        val tsc = TimeSeriesCount(now, metrics)
        EventBus.publish(MessageEvent("payload", Payload(Json.toJson(tsc), "counters")))
      }

      case TimerEvent(actorName, time) => {
        MetricsCollector.find(MetricType.Timer, actorName)
          .getOrElse(MetricsCollector.newMetricTimer(actorName))
          .asInstanceOf[MetricTimer]
          .update(time)
      }

      case CounterEvent(actorName, count) => {
        MetricsCollector.find(MetricType.Counter, actorName)
          .getOrElse(MetricsCollector.newMetricCounter(actorName))
          .asInstanceOf[MetricCounter]
          .incrementBy(count)
      }

    }

    override def preStart() {
      super.preStart()
      Logger.info(s"StatisticsActor Ready - ${name}, path - ${self.path}")

    }

    override def postStop() {
      super.postStop()
      Logger.info(s"StatisticsActor ShutDown - ${name}, path - ${self.path}")
    }
  }
}
