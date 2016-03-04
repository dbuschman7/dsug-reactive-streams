package me.lightspeed7.dsug.streams

import org.scalatest.FunSuite
import org.scalatest.Matchers._
import akka.stream.scaladsl._
import akka.stream.ClosedShape

class PascalTriangleTest extends FunSuite {

  test("Test the meat of the algorithm") {

    val input = Seq(Seq(1L), Seq(1L, 1), Seq(1L, 2, 1), Seq(1L, 3, 3, 1))
    val output = Seq(Seq(1L, 1), Seq(1L, 2, 1), Seq(1L, 3, 3, 1), Seq(1L, 4, 6, 4, 1))

    val g = RunnableGraph.fromGraph(GraphDSL.create() { implicit builder: GraphDSL.Builder[Unit] =>
      import GraphDSL.Implicits._

      val source = Source.fromIterator { () => input.iterator }
      val sink = Sink.fold[Seq[Seq[Long]], Seq[Long]](Seq()) { (a, b) => a :+ b }

      source ~> nextPascal ~> sink

      ClosedShape
    })

  }
}