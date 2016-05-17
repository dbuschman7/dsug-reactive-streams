package me.lightspeed7.dsug.streams

package object pascal {

  import scala.concurrent.Future
  import akka.stream.ActorMaterializer
  import akka.stream.scaladsl._

  class Pascal(val coefficients: Seq[Long]) extends AnyVal {
    def ++ : Pascal = new Pascal((0L +: coefficients :+ 0L).sliding(2).toSeq.map(_.sum))
    override def toString: String = coefficients.mkString("Pascal( ", ", ", " )")
  }
  object Pascal {
    def apply(in: Long*) = new Pascal(in.toSeq)
  }

  def source(input: Seq[Pascal]) = Source.fromIterator { () => input.iterator }

  def nextPascal = Flow[Pascal].map(_++)

  def sink = Sink.fold[Seq[Pascal], Pascal](Seq()) { (a, b) => a :+ b }

  def createFlow(input: Seq[Pascal])(implicit m: ActorMaterializer): Future[Seq[Pascal]] = {
    source(input).via(nextPascal).runWith(sink)
  }

}