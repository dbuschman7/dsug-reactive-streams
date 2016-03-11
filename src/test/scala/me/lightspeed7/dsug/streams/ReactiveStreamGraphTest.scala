package me.lightspeed7.dsug.streams

import org.scalatest.FunSuite
import akka.actor.ActorSystem
import akka.stream.scaladsl.Sink
import akka.stream.scaladsl._
import akka.stream._
import scala.concurrent.Await
import scala.concurrent.duration._

class ReactiveStreamGraphTest extends FunSuite {

  implicit val system = ActorSystem("Sys")
  implicit val ec = system.dispatcher
  implicit val materializer = ActorMaterializer.create(system)

  test("Create a Reactive Stream in a Graph Shape") {

    val throttling: Source[Long, akka.NotUsed] = createThrottle(500)
    val oscillator = new Oscillator(1).createSource

    val processor = Flow[Long].map{ in => in + 2 } 
    
 
    val graph = GraphDSL.create(printSink(1), printSink(2))((m, _) => m) { implicit builder =>
      (sink1, sink2) =>
        import GraphDSL.Implicits._

        val zip = builder.add(Zip[Long, Long]())
        
        val unzip = builder.add(Flow[(Long, Long)].map(_._2))
        
        val split = builder.add(Balance[Long](2))

        throttling ~> zip.in0
        oscillator ~> zip.in1
        /*         */ zip.out ~> unzip ~> processor ~> split.in
        /*                                          */ split.out(0) ~> sink1
        /*                                          */ split.out(1) ~> sink2

        ClosedShape
    }

    val rg = RunnableGraph.fromGraph(graph)

    val decider: Supervision.Decider = {
      case _: ArithmeticException => Supervision.Resume
      case _                      => Supervision.Stop
    }

    val settings = ActorMaterializerSettings(system).withSupervisionStrategy(decider)
    val materializer = ActorMaterializer(settings)
    rg.run()(materializer)

    Thread.sleep(10 * 1000) // run the system for awhile and then shut it down.
    Await.result(system.terminate(), 5 seconds)
  }
}