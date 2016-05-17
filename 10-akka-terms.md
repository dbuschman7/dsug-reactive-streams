# Akka Glossary

### Actors
* Actors are the smallest unit when building an application. 
* Form hierarchies called Actor Systems

## Actor Systems 
* actors naturally form hierarchies. 
* ACtors higher hierarchy can supervise their children

### Mailboxes
* Each Actor has a mailbox. 
* stores messages that an actors need to process. 
* Sending messages to an actor is asynchronous
* Implemented as a thread safe queue
* Enqueuing happens in the time-order of send operations, which means that messages sent from different actors may not have a defined order at runtime due to the apparent randomness of distributing actors across threads. Sending multiple messages to the same target from the same actor, on the other hand, will enqueue them in the same order.
 

### Supervisor Strategy
The final piece of an actor is its strategy for handling faults of its children. Fault handling is then done transparently by AKKA, but can be customized

###Concurrency vs. Parallelism
Concurrency and parallelism are related concepts, but there are small differences. 
* Concurrency means that two or more tasks are making progress even though they might not be executing simultaneously. 
* Parallelism on the other hand arise when the execution can be truly simultaneous.

###Asynchronous vs. Synchronous
* Synchronous - if the caller cannot make progress until the method returns a value or throws an exception. 
* Asynchronous - if the call allows the caller to progress after a finite number of steps, and the completion of the method may be signalled via some additional mechanism (it might be a registered callback, a Future, or a message).

###Non-blocking vs. Blocking
* Blocking - if the delay of one thread can indefinitely delay some of the other threads. 
A good example is a resource which can be used exclusively by one thread using mutual exclusion. 
If a thread holds on to the resource indefinitely (for example accidentally running an infinite loop) other threads waiting on the resource can not progress. 

* Non-blocking - means that no thread is able to indefinitely delay others.

* Non-blocking operations are preferred to blocking ones, as the overall progress of the system is not trivially guaranteed when it contains blocking operations.

[Next >> Akka Streams Glossary](20-akka-stream-terms.md) 