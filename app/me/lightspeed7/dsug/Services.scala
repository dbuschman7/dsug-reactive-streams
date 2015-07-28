package me.lightspeed7.dsug

import me.lightspeed7.dsug.ui.StatisticsActor
import play.api.Logger

object Actors {
  import play.libs.Akka
  import akka.actor.ActorRef
  import akka.actor.Props
  import me.lightspeed7.dsug.generator.GeneratorActor

  private[dsug] lazy val system = Akka.system()

  lazy val generator: ActorRef = system.actorOf(Props(classOf[GeneratorActor], "GeneratorActor"))

  lazy val statistics: ActorRef = system.actorOf(Props(classOf[StatisticsActor], "StatisticsActor"))
  def start = {
    generator
    // more here
    Logger.info("Actors - Generator started")
  }

  def stop = system.terminate()

}


