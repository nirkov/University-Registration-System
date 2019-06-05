#  University Registration System - Actor Thread Pool Patern

## Introduction
In Actor thread pool, each actor has a queue of actions. One can submit new actions for actors and the pool makes sure that each actor will run its actions in the order they were received, while not blocking other threads. The threads in the pool are assigned dynamically to the actors.

## Actor Thread Pool

### Detailed Description
In actor thread pool, each actor has a queue of actions. One can submit a new action to the actor. The threads in the actor thread pool are assigned dynamically to the actors, in the following way. Each thread searches for an action to execute in all the actors' queues. Once the thread found such an action, it will prevent any other thread from fetching actions from that queue. Once it nished executing the action, it will allow other threads to fetch actions from this queue. And that thread will try to nd another action to execute. Note, although a thread prevents other threads from processing actions from the queue which it is executing an action from it, the other threads are not blocked. The threads have to search for an action from other queues, and only if all the queues are empty or not available (threads work on them) then the threads will go sleep and should wake up once an action from an available queue is ready to be fetched. An important observation, in Actor Thread Pool, the amount of actors can be significantly greater than the amount of threads.

### Design Pattern: Event Loop
In this assignment you will implement the Event Loop design pattern. In such pattern, each thread in the Thread Pool has a loop. In each iteration of the loop, the thread tries to fetch an action and execute it. An important point in such design pattern is not to block the thread for a long time, unless there is no work for it. Blocking the thread for a long time while there is a work for it will have a bad effiect in your implementation, since threads are idle although there is work for them.

### Actors and Actions
An action is a computational task. An actor is a computational entity that in response to actions it receives, an actor can: make local decisions, create more actors, send message to other actors. Actors may modify private state, but can only affect each other through messages. In this assignment, the messages between actors are actions. An actor can submit an action to another actor's queue. A thread will fetch this action from the receiver queue and execute it. Note, that actors submit actions to another actor's queue, when that action may affect the private state of the receiver's queue, and by that avoiding locks since only one thread can access the private state of an actor at the same time. You must not synchronize on the state of the actor, but you can use the state for the implementation of your Actor Thread Pool.

![image](https://user-images.githubusercontent.com/32679759/58989924-83381380-87ed-11e9-9236-3ccbe7e59e64.png)

### Dependency Between Actions and Using Promises
In some applications the actions have interdependence constraints. The thread pool should fulfill these constraints. The actions execution should be suspended until the actions it depends on are completed. Suspending an action should not suspend the thread or the actor. When an action is suspended, the thread should continue with another action, and the actor's queue should be available for fetching actions by any thread. The suspended action should be eventually continued only when all actions it waits for them are done. The approach to handle this is to enqueue the continuation of the resumed action on the same actor's queue whenever it is ready to be continued. In some point, one thread will fetch the continuation and execute it.

### Promise Design Pattern
In order to enable the mentioned above. We use the promise design pattern (Using the Promise class). A Promise is used for deferred computations and represents the result of an operation that has not been completed yet. Each action has a Promise Object which hold its result, whenever an action needs a result of another action, it passes to the action's Promise object a callback. This callback will be executed once the event is completed.
