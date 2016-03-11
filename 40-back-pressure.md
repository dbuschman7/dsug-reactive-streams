# Back-pressure explained
* Akka Streams implement an asynchronous non-blocking back-pressure protocol standardised by the Reactive Streams specification, which Akka is a founding member of.

* The user of the library does not have to write any explicit back-pressure handling code — it is built in and dealt with automatically by all of the provided Akka Streams processing stages. It is possible however to add explicit buffer stages with overflow strategies that can influence the behaviour of the stream. This is especially important in complex processing graphs which may even contain loops (which must be treated with very special care, as explained in Graph cycles, liveness and deadlocks).

* The back pressure protocol is defined in terms of the number of elements a downstream **Subscriber** is able to receive and buffer, referred to as demand. The source of data, referred to as **Publisher** in Reactive Streams terminology and implemented as **Source** in Akka Streams, guarantees that it will never emit more elements than the received total demand for any given **Subscriber**.

**NOTE :**The Reactive Streams specification defines its protocol in terms of Publisher and Subscriber. These types are not meant to be user facing API, instead they serve as the low level building blocks for different Reactive Streams implementations.


![Back pressure](graph_stage_conceptual1.png)


### Next : Demo !!
