package me.lightspeed7.dsug.reactive

package Boot

import scala.concurrent.duration._

import akka.actor._
import akka.stream._
import akka.stream.actor._
import akka.stream.actor.ActorSubscriberMessage._
import akka.stream.scaladsl._
import akka.stream.scaladsl.FlowGraph.Implicits._

import me.lightspeed7.dsug.Actors
import me.lightspeed7.dsug.reactive.Monitoring

object Scenarios {

  implicit val system = ActorSystem("Sys")
  implicit val materializer = ActorMaterializer()

  /**
   * 1. Fast publisher, Faster consumer
   * - publisher with a map to send, and a throttler (e.g 50 msg/s)
   * - Result: publisher and consumer rates should be equal.
   */
  def scenario1: RunnableGraph[Unit] = {
    FlowGraph.closed() { implicit builder =>

      import FlowGraph.Implicits._

      // get the elements for this flow.
      val source = throttledSource(1 second, 20 milliseconds, 20000, "fastProducer")
      val fastSink = Sink.actorSubscriber(Props(classOf[DelayingActor], "fastSink"))

      // connect source to sink
      source ~> fastSink
    }
  }

  /**
   * 2. Fast publisher, fast consumer in the beginning get slower, no buffer
   * - same publisher as step 1. (e.g 50msg/s)
   * - consumer, which gets slower (starts at no delay, increase delay with every message.
   * - Result: publisher and consumer will start at same rate. Publish rate will go down
   * together with publisher rate.
   * @return
   */
  def scenario2: RunnableGraph[Unit] = {
    FlowGraph.closed() { implicit builder =>

      import FlowGraph.Implicits._

      // get the elements for this flow.
      val source = throttledSource(1 second, 20 milliseconds, 10000, "fastProducer")
      val slowingSink = Sink.actorSubscriber(Props(classOf[SlowDownActor], "slowingDownSink", 10l))

      // connect source to sink
      source ~> slowingSink
    }
  }

  /**
   * 3. Fast publisher, fast consumer in the beginning get slower, with drop buffer
   * - same publisher as step 1. (e.g 50msg/s)
   * - consumer, which gets slower (starts at no delay, increase delay with every message.
   * - Result: publisher stays at the same rate, consumer starts dropping messages
   */
  def scenario3: RunnableGraph[Unit] = {
    FlowGraph.closed() { implicit builder =>

      import FlowGraph.Implicits._

      // first get the source
      val source = throttledSource(1 second, 30 milliseconds, 6000, "fastProducer")
      val slowingSink = Sink.actorSubscriber(Props(classOf[SlowDownActor], "slowingDownSinkWithBuffer", 20l))

      // now get the buffer, with 100 messages, which overflow
      // strategy that starts dropping messages when it is getting
      // too far behind.
      //      val buffer = Flow[Int].buffer(3000, OverflowStrategy.dropHead)
      val buffer = Flow[Int].buffer(1000, OverflowStrategy.backpressure)

      // connect source to sink with additional step
      source ~> buffer ~> slowingSink
    }
  }

  /**
   * 4. Fast publisher, 2 fast consumers, one consumer which gets slower
   * - Result: publisher rate and all consumer rates go down at the same time
   */
  def scenario4: RunnableGraph[Unit] = {
    FlowGraph.closed() { implicit builder =>

      import FlowGraph.Implicits._

      // first get the source
      val source = throttledSource(1 second, 20 milliseconds, 9000, "fastProducer")

      // and the sinks
      val fastSink = Sink.actorSubscriber(Props(classOf[DelayingActor], "broadcast_fastsink", 0l))
      val slowingDownSink = Sink.actorSubscriber(Props(classOf[SlowDownActor], "broadcast_slowsink", 20l))

      // and the broadcast
      val broadcast = builder.add(Broadcast[Int](2))

      // use a broadcast to split the stream
      source ~> broadcast ~> fastSink
      broadcast ~> slowingDownSink
    }
  }

  /**
   * 5. Fast publisher, 2 fast consumers, one consumer which gets slower but has buffer with drop
   * - Result: publisher rate and fast consumer rates stay the same. Slow consumer goes down.
   */
  def scenario5: RunnableGraph[Unit] = {
    FlowGraph.closed() { implicit builder =>

      import FlowGraph.Implicits._

      // first get the source
      val source = throttledSource(1 second, 20 milliseconds, 9000, "fastProducer")

      // and the sinks
      val fastSink = Sink.actorSubscriber(Props(classOf[DelayingActor], "fastSink", 0l))
      val slowingDownSink = Sink.actorSubscriber(Props(classOf[SlowDownActor], "slowSink", 30l))
      //      val buffer = Flow[Int].buffer(300, OverflowStrategy.dropTail)
      val buffer = Flow[Int].buffer(3500, OverflowStrategy.backpressure)

      // and the broadcast
      val broadcast = builder.add(Broadcast[Int](2))

      // connect source to sink with additional step
      source ~> broadcast ~> fastSink
      /*      */ broadcast ~> buffer ~> slowingDownSink
    }
  }

  /**
   * 6. Fast publisher (50msg/s), 2 consumer which total 70msg/s, one gets slower with balancer
   * - Result: slowly more will be processed by fast one. When fast one can't keep up, publisher
   * will slow down*
   */
  def scenario6: RunnableGraph[Unit] = {
    FlowGraph.closed() { implicit builder =>

      import FlowGraph.Implicits._

      // first get the source
      val source = throttledSource(1 second, 10 milliseconds, 20000, "fastProducer")

      // and the sin
      val fastSink = Sink.actorSubscriber(Props(classOf[DelayingActor], "fastSinkWithBalancer", 12l))
      val slowingDownSink = Sink.actorSubscriber(Props(classOf[SlowDownActor], "slowingDownWithBalancer", 14l, 1l))
      val balancer = builder.add(Balance[Int](2))

      // connect source to sink with additional step
      source ~> balancer ~> fastSink
      balancer ~> slowingDownSink
    }
  }

  /**
   * Create a source which is throttled to a number of message per second.
   */
  case class Tick()

  def throttledSource(delay: FiniteDuration, interval: FiniteDuration, numberOfMessages: Int, name: String): Source[Int, Unit] = {
    Source[Int]() { implicit b =>
      import FlowGraph.Implicits._

      // two source
      val tickSource = Source(delay, interval, Tick())
      val rangeSource = Source(1 to numberOfMessages)

      // we collect some metrics during processing so we can count the rate
      val sendMap = b.add(Flow[Int].map({ x => Actors.statistics ! CounterEvent(name); x }))

      // we use zip to throttle the stream
      val zip = b.add(Zip[Tick, Int]())
      val unzip = b.add(Flow[(Tick, Int)].map(_._2))

      // setup the message flow
      tickSource ~> zip.in0
      rangeSource ~> zip.in1
      zip.out ~> unzip ~> sendMap

      sendMap.outlet
    }
  }
}

class DelayingActor(name: String, delay: Long = 0) extends ActorSubscriber with Monitoring {
  override protected def requestStrategy: RequestStrategy = OneByOneRequestStrategy

  actorName = name

  override def receive: Receive = {
    case OnNext(msg: Int) => Thread.sleep(delay)

    case OnComplete       => println("DelayingActor - OnComplete called")
  }
}

class SlowDownActor(name: String, delayPerMsg: Long = 0, initialDelay: Long = 0) extends ActorSubscriber with Monitoring {
  override protected def requestStrategy: RequestStrategy = OneByOneRequestStrategy

  actorName = name

  var delay = 0l

  override def receive: Receive = {

    case OnNext(msg: Int) =>
      delay += delayPerMsg
      Thread.sleep(initialDelay + (delay / 1000), delay % 1000 toInt)
    case _ =>
  }
}

