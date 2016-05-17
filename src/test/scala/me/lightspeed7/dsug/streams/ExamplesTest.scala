package me.lightspeed7.dsug.streams

import scala.concurrent._
import scala.concurrent.duration.DurationInt

import org.scalatest._
import org.scalatest.Matchers._

import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._
import java.util.concurrent.atomic.AtomicLong

class ExamplesTest extends FunSuite with BeforeAndAfterAll {

  // Examples Test
  val source1 = Source[Long](1L to 10)
  val source2 = Source[Long](List(1L, 2, 3))
  val source3 = Source.fromFuture(Future.successful("Hello Streams!"))
  val source4 = Source.single("only one element")
  val source5 = Source.empty
  val source6 = Source.fromIterator { () => Stream.iterate(1L)(_ + 1).iterator }

  val sink1 = Sink.foreach[Long](println(_))
  val sink2 = Sink.ignore
  val sink3 = Sink.fold[Long, Long](0)(_ + _)

  val flow1 = Flow[Long].map(_ * 2)
  val flow2 = Flow[Long].filter(_ % 2 == 0)
  val flow3 = Flow[Long].mapAsync(2) { in => Future.successful[Long](in * 2) }
  val flow4 = Flow[Long].map(_ * 2).buffer(1000, OverflowStrategy.backpressure)
  val flow5 = Flow[Long].map { l => Thread.sleep(100); l }

  // Setup 
  implicit val system = ActorSystem("Sys")
  implicit val ec = system.dispatcher
  implicit val materializer = ActorMaterializer.create(system)

  def await[T](f: Future[T]): T = Await.result(f, 15 seconds)

  override def afterAll = await(system.terminate())

  // Test
  test("1 to 10 time 2 - println") {

    val flow = source1
      .via(flow1)
      .runWith(sink1)

    val f = await(flow)

    f should be(akka.Done)
  }

}