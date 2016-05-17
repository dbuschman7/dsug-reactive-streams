package me.lightspeed7.dsug.streams.pascal

import org.scalatest.BeforeAndAfterAll
import org.scalatest.FunSuite
import org.scalatest.Matchers._

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration._

class PascalTest extends FunSuite with BeforeAndAfterAll {

  implicit val system = ActorSystem("Sys")
  implicit val ec = system.dispatcher
  implicit val materializer = ActorMaterializer.create(system)

  override def afterAll = Await.result(system.terminate(), 5 seconds)

  // Test 
  test("Test the meat of the algorithm") {

    val input = Seq(Pascal(1L), Pascal(1L, 1), Pascal(1L, 2, 1), Pascal(1L, 3, 3, 1))

    val flow: Future[Seq[Pascal]] = createFlow(input)

    val results = Await.result(flow, 5 seconds)

    // validate 
    println(input)
    val output = Seq(Pascal(1L, 1), Pascal(1L, 2, 1), Pascal(1L, 3, 3, 1), Pascal(1L, 4, 6, 4, 1))
    println(output)
    results should be(output)

  }
}