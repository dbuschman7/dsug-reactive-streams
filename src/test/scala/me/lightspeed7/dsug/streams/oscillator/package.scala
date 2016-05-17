package me.lightspeed7.dsug.streams

package object oscillator {

  import akka.stream.scaladsl._

  class Oscillator(start: Long) {

    case class SquareWave(
        shift: Double = 0.0,
        amplitude: Double = 1.0,
        period: Int = 10
    ) { // number of iterations for a full cycle period 

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