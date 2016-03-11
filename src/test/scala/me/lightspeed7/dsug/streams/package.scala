package me.lightspeed7.dsug

import akka.stream.scaladsl._
import scala.concurrent.Future
import akka.stream.stage.GraphStage
import akka.stream.FlowShape
import akka.stream.impl.FanOut
import akka.stream.FanOutShape2
import akka.stream.Inlet
import akka.stream.Outlet
import akka.stream.FanOutShape
import akka.stream.Attributes
import akka.stream.stage.GraphStageLogic
import akka.stream.stage.InHandler
import akka.stream.stage.OutHandler
import akka.stream.OverflowStrategy

package object streams {

  // Examples Test
  val source1 = Source(1 to 10)
  val source2 = Source(List(1, 2, 3))
  val source3 = Source.fromFuture(Future.successful("Hello Streams!"))
  val source4 = Source.single("only one element")
  val source5 = Source.empty
  val source6 = Source.fromIterator { () => Stream.iterate(1L)(_ + 1).iterator }

  val sink1 = Sink.foreach[String](println(_))
  val sink2 = Sink.ignore
  val sink3 = Sink.fold[Int, Int](0)(_ + _)

  val nextPascal = Flow[Seq[Long]].map { prev => (0L +: prev :+ 0L).sliding(2).toSeq.map(_.sum) }

  val flow1 = Flow[Long].map(_ * 2)
  val flow2 = Flow[Long].filter(_ % 2 == 0)
  val flow3 = Flow[Long].mapAsync(2) { in => Future.successful[Long](in * 2) }
  val flow4 = Flow[Long].map(_ * 2).buffer(1000, OverflowStrategy.backpressure)

  // Graph Test
  def createThrottle(ratePerMinute: Int) = Source.fromIterator[Long] { () => Stream.continually({ Thread.sleep(60 * 1000 / ratePerMinute); 1L }).iterator }

  def printSink(num: Int) = Sink.foreach { in: Long => println(s"Sink${num} - ${in}") }

  class Oscillator(start: Long) {

    case class SquareWave(
        shift: Double = 0.0,
        amplitude: Double = 1.0,
        period: Int = 10) { // number of iterations for a full cycle period 

      private val positive = period / 2
      private var current: Int = 0

      def next: Double = {
        val sign = (0.5 - (current / positive)) * 2
        current = (current + 1) % period
        (sign * amplitude / 2) + shift
      }
    }

    private lazy val gen = SquareWave(0.0, 2.0, 20)

    lazy val stream: Stream[Long] = {
      val s = start #:: stream.scanLeft(start + 1) { (prev, cur) => prev + gen.next.toLong }
      gen.next // force the first value out 
      s
    }

    def createSource: Source[Long, akka.NotUsed] = Source.fromIterator { () => stream.iterator }
  }

}