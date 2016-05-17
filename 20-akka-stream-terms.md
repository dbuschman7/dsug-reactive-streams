# Akka Streams Glossary
Akka Streams is a : 
* library to process and transfer a sequence of elements using bounded buffer space. 
This latter property is what we refer to as boundedness 
 it is the defining feature of Akka Streams. 
 
 * Expressed as a chain (or as we see later, graphs) of processing entities 
 * each executing independently (and possibly concurrently) from the others 
 * only buffering a limited number of elements at any given time (bounded). 
 * Bounded buffers is one of the differences from the actor model, where each actor usually has an unbounded, or a bounded, but dropping mailbox. 
 * Akka Stream processing entities have bounded "mailboxes" that do not drop.



##Stream
An active process that involves moving and transforming data.

##Element
* An element is the processing unit of streams. 
* All operations transform and transfer elements from upstream to downstream. 
* Buffer sizes are always expressed as number of elements independently form the actual size of the elements.

##Back-pressure
* A means of flow-control 
* A way for consumers of data to notify a producer about their current availability
* Effectively slowing down the upstream producer to match their consumption speeds. 
* Back-pressure is always understood as non-blocking and asynchronous.

##Graph
* Description of a stream processing topology
* Defining the pathways through which elements shall flow when the stream is running.

##Processing Stage
The common name for the building blocks that build up a Graph. 

## Stream Materialization
* Want you create is a blueprint, an execution plan. 
* The process of taking a blueprint (the graph) and converting it to actors. 
* Starting up Actors which power the processing in the Actor System.
* Allocates all the necessary resources it needs in order to run. 
 * Could also mean opening files 
 * socket connections etc. 
* Initialize the stream and initializing demand.

[Next >> Linear Flows](30-linear-flows.md) 
