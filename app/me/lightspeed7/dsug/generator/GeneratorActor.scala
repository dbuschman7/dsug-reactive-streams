package me.lightspeed7.dsug.generator
import scala.concurrent.duration._
import scala.util.Random

import akka.actor.Actor
import me.lightspeed7.dsug._
import play.api.Logger

class GeneratorActor(name: String) extends Actor {

  private val Tick = "tick" // scheduler tick

  implicit val ec = Actors.system.dispatcher

  private val cancellable: akka.actor.Cancellable = //
    Actors.system.scheduler.schedule(5 seconds, Config.LogGeneratorDelay, Actors.generator, Tick)

  private val random = new Random()

  private var i = 0L

  import me.lightspeed7.dsug.Config._
  def receive = {
    case Tick => {
      i = i + 1
      if (i % 100 == 0) {
        Logger.debug(s"Sent $i messages!")
        Actors.statistics ! Counts("rawCount", 100)
      }
    }
  }

  override def preStart() {
    super.preStart()
    Logger.info("GeneratorActor Ready")
  }

  override def postStop() {
    if (cancellable != null) {
      cancellable.cancel
    }
    super.postStop()
    Logger.info("GeneratorActor")
  }
}
