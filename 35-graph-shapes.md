# Non-Linear Graph Shapes


<img src="compose_graph1.png" width="800"  />



## Additional Shapes 

### Fan-out
* **Broadcast[T]** – (1 input, N outputs) given an input element emits to each output
* **Balance[T]** – (1 input, N outputs) given an input element emits to one of its output ports
* **UnzipWith[In,A,B,...]** – (1 input, N outputs) takes a function of 1 input that given a value for each input emits N output elements (where N <= 20)
* **UnZip[A,B]** – (1 input, 2 outputs) splits a stream of **(A,B)** tuples into two streams, one of type A and one of type B

## Fan-in
* **Merge[In]** – (N inputs , 1 output) picks randomly from inputs pushing them one by one to its output
* **MergePreferred[In]** – like Merge but if elements are available on preferred port, it picks from it, otherwise randomly from others
* **ZipWith[A,B,...,Out]** – (N inputs, 1 output) which takes a function of N inputs that given a value for each input emits 1 output element
* **Zip[A,B]** – (2 inputs, 1 output) is a ZipWith specialised to zipping input streams of A and B into an (A,B) tuple stream
* **Concat[A]** – (2 inputs, 1 output) concatenates two streams (first consume one, then the second one)

## Example 

![Simple DSL](graph-dsl.png)
```scala
val g = RunnableGraph.fromGraph(GraphDSL.create() { implicit builder: GraphDSL.Builder[Unit] =>
  import GraphDSL.Implicits._
  val in = Source(1 to 10)
  val out = Sink.ignore
 
  val bcast = builder.add(Broadcast[Int](2))
  val merge = builder.add(Merge[Int](2))
 
  val f1, f2, f3, f4 = Flow[Int].map(_ + 10)
 
  in ~> f1 ~> bcast ~> f2 ~> merge ~> f3 ~> out
  /*       */ bcast ~> f4 ~> merge
  
  ClosedShape
})
```

[Next >> Back Pressure](40-back-pressure.md) 
