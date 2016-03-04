package me.lightspeed7.dsug

import akka.stream.scaladsl._
import scala.concurrent.Future

package object streams {

  // Examples 
  val source1 = Source(1 to 10)
  val source2 = Source(List(1, 2, 3))
  val source3 = Source.fromFuture(Future.successful("Hello Streams!"))
  val source4 = Source.single("only one element")
  val source5 = Source.empty
  val source6 = Source.fromIterator { () => Stream.iterate(1L)(_ + 1).iterator }

  val sink1 = Sink.foreach[String](println(_))
  val sink2 = Sink.ignore
  val sink3 = Sink.fold[Int, Int](0)(_ + _)

  val flow1 = Flow[Long].map(_ * 2)
  val flow2 = Flow[Long].filter(_ % 2 == 0)
  val flow3 = Flow[Long].mapAsync(2) { in => Future.successful[Long](in * 2) }

  // Pascal's Triangle
  val nextPascal = Flow[Seq[Long]].map { prev => (0L +: prev :+ 0L).sliding(2).toSeq.map(_.sum) }

  // Shakespeare

}