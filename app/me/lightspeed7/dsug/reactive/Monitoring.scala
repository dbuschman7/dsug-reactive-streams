package me.lightspeed7.dsug.reactive

import akka.contrib.pattern.ReceivePipeline
import me.lightspeed7.dsug.Actors

case class TimerEvent(actorName: String, value: Float);
case class CounterEvent(actorName: String, value: Long = 1);

trait Monitoring extends ReceivePipeline {

  var actorName = self.path.name;

  pipelineOuter(
    inner => {
      case x =>
        Actors.statistics ! CounterEvent(actorName)
        val start = System.nanoTime();
        inner(x)
        Actors.statistics ! TimerEvent(actorName, System.nanoTime() - start);
    })

}


