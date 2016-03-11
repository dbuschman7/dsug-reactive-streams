package me.lightspeed7.dsug.streams

import org.scalatest.FunSuite
import org.scalatest.Matchers._
import akka.stream.scaladsl._
import akka.stream.ClosedShape
import scala.concurrent.Future
import akka.actor.ActorSystem
import org.scalatest.BeforeAndAfterAll
import akka.stream.ActorMaterializer
import scala.concurrent.duration._
import scala.concurrent.Await
import scala.concurrent.duration._

class StreamTest extends FunSuite with BeforeAndAfterAll {

  implicit val system = ActorSystem("Sys")
  implicit val ec = system.dispatcher
  implicit val materializer = ActorMaterializer.create(system)

  override def afterAll = Await.result(system.terminate(), 5 seconds)

  test("Test the meat of the algorithm") {

    val input = Seq(Seq(1L), Seq(1L, 1), Seq(1L, 2, 1), Seq(1L, 3, 3, 1))
    val source = Source.fromIterator { () => input.iterator }

    val output = Seq(Seq(1L, 1), Seq(1L, 2, 1), Seq(1L, 3, 3, 1), Seq(1L, 4, 6, 4, 1))
    val sink = Sink.fold[List[List[Long]], Seq[Long]](List()) { (a, b) => (a :+ b.toList).toList }

    val results = Await.result(source.via(nextPascal).runWith(sink), 5 seconds)
    println(results)
    println(output)
    results should be(output)

  }

  test("Test the oscilator") {
    val osc = new Oscillator(1)
    val expected = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 2)
    osc.stream.take(22).toList should be(expected)
  }

}