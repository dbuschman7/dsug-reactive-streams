# Akka Streams 
Akka Streams is a library to process and transfer a sequence of elements using bounded buffer space. This latter property is what we refer to as boundedness and it is the defining feature of Akka Streams. Translated to everyday terms it is possible to express a chain (or as we see later, graphs) of processing entities, each executing independently (and possibly concurrently) from the others while only buffering a limited number of elements at any given time. This property of bounded buffers is one of the differences from the actor model, where each actor usually has an unbounded, or a bounded, but dropping mailbox. Akka Stream processing entities have bounded "mailboxes" that do not drop.



##Stream
An active process that involves moving and transforming data.

##Element
An element is the processing unit of streams. All operations transform and transfer elements from upstream to downstream. Buffer sizes are always expressed as number of elements independently form the actual size of the elements.

##Back-pressure
A means of flow-control, a way for consumers of data to notify a producer about their current availability, effectively slowing down the upstream producer to match their consumption speeds. In the context of Akka Streams back-pressure is always understood as non-blocking and asynchronous.

##Non-Blocking
Means that a certain operation does not hinder the progress of the calling thread, even if it takes long time to finish the requested operation.

##Graph
A description of a stream processing topology, defining the pathways through which elements shall flow when the stream is running.

##Processing Stage
The common name for all building blocks that build up a Graph. 

## Stream Materialization

When constructing flows and graphs in Akka Streams think of them as preparing a blueprint, an execution plan. Stream materialization is the process of taking a stream description (the graph) and allocating all the necessary resources it needs in order to run. In the case of Akka Streams this often means starting up Actors which power the processing, but is not restricted to that—it could also mean opening files or socket connections etc.—depending on what the stream needs.


