# :mortar_board: University Registration System :mortar_board:


## Introduction
In Actor thread pool, each actor has a queue of actions. One can submit new actions for actors and the pool makes sure that each actor will run its actions in the order they were received, while not blocking other threads. The threads in the pool are assigned dynamically to the actors.

## Part 1: Actor Thread Pool Pattern

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

![image](https://user-images.githubusercontent.com/32679759/58990515-b8913100-87ee-11e9-92e5-194911f8f6af.png)

### Summary

- **Action**: an abstract class that represents an action. An action is an object which holds the required information to handle an action in the system. The action also holds a Promise<R> object which will hold its result. The main action methods are:
  - start: This is the abstract method, that should be implemented for each action type, it implements the action behavior.
  - sendMessage: This methods submit an action (message) to other actor.
  - then: add a callback to be executed once all the given action are completed. 
  - complete: resolves the internal result - should be called by the action derivative once it is done. 
  - getResult: returns the task promised result.
  
- **Promise**: this class represents a promise result i.e., an object that eventually will be resolved to hold a result of some operation, the class allows for getting the result once it is available and registering a callback that will be called once the result is available. Promise includes these methods:
  - get: return the resolved value if such exists. 
  - resolve: called upon completing the operation, it sets the result of the operation to a new value, and trigger all the subscribed callbacks.
  - subscribe: add a callback to be called when this object is resolved if while calling this method the object is already resolved- the callback should be called immediately.
  - isResolved: return true if this object has been resolved.
  
- **VersionMonitor**: Describes a monitor that supports the concept of versioning - its idea is simple, the monitor has a version number which you can receive via the method getVersion() once you have a version number, you can call await() with this version number in order to wait until this version number changes. You can also increment the version number by one using the inc() method.

- **ActorThreadPool**: manages all the the actor and threads in our system. The constructor of ActorThreadPool creates an ActorThreadPool which has n threads. The ActorThreadPool includes these methods:
  - submit: Enqueues an action to an actor's queue. If the actor is not present in the system, it will create it.
  - start: start the threads belongs to this thread pool. 
  - shutdown: closes the thread pool - this method interrupts all the threads and wait for them to stop - it returns only when there are no live threads in the queue. After calling this method, one should not use the queues anymore
  
![image](https://user-images.githubusercontent.com/32679759/58992912-bd58e380-87f4-11e9-937d-5ed8cc1f399b.png)


## Part 2: University Management System

The university has a set of departments, each department offers a set of courses to students (of all the departments) and each student can register for courses if he meets the prerequisites and there is an available space for him. The register/unregister request are considered until the end of the registration period. In this System we define Actors and their Private States.

### Program Flow
The program is divided into three phases. Once a phase is completed, you will proceed to the next phase. 
- Phase 1: All the open course actions appear in Phase 1. There might appear other actions as well. 
- Phase 2: Any action can appear.
- Phase 3: Any action can appear.

### Actors
In order to simulate the university, you should create three types of actors: 
- An actor per student. 
- An actor per course. 
- An actor per department's secretary.

### Private States Of Actors
Each actor should maintain in its private state a log of all the action it has preformed. In addition, the private states include: 
- Department: A department's private state includes list of courses and list of students in all the department.
- Course: A course's private state includes number of available spaces and list of students in the course, number of registered students, and prerequisites.
- Student: A student's private state includes grades sheet, and department’s signature. In the grades sheet appear all the courses the students learnt along with his grades.

### Logging Actions
In its private state, the actor maintains a list of all the actions it has executed. The list holds only the description of the action as it is shown in the JSON file in section 4.9.2. Example: "Open Course", "Add Student", etc.

### Actions Following is a list of actions that you should enable.

- **Open A New Course**: 
  - Behavior: This action opens a new course in a specified department. The course has an initially available spaces and a list of prerequisites.
  - Actor: Must be initially submitted to the Department's actor.
- **Add Student**: 
  - Behavior: This action adds a new student to a specified department. 
  - Actor: Must be initially submitted to the Department's actor.
- **Participating In Course**: 
  - Behavior: This action should try to register the student in the course, if it succeeds, should add the course to the grades sheet of the student, and give him a grade if supplied. See the input example.
  - Actor: Must be initially submitted to the course's actor.
- **Unregister**: 
  - Behavior: If the student is enrolled in the course, this action should unregister him (update the list of students of course, remove the course from the grades sheet of the student and increases the number of available spaces).
  - Actor: Must be initially submitted to the course's actor. 
- **Close A Course**: 
  - Behavior: This action should close a course. Should unregister all the registered students in the course and remove the course from the department courses' list and from the grade sheets of the students. The number of available spaces of the closed course should be updated to -1. DO NOT remove its actor. After closing the course, all the request for registration should be denied.
  - Actor: Must be initially submitted to the department's actor.
- **Opening New places In a Course**: 
  - Behavior: This action should increase the number of available spaces for the course. 
  - Actor: Must be initially submitted to the course's actor.
- **Check Administrative Obligations**: 
  - Behavior: The department's secretary have to allocate one of the computers available in the warehouse, and check for each student if he meets some administrative obligations. The computer generates a signature and save it in the private state of the students.
  - Actor: Must be initially submitted to the department's actor.
- **Announce about the end of registration period**: 
  - Behavior: From this moment, reject any further changes in registration. And, close courses with number of students less than 5.
  - Actor: Must be initially submitted to the department's actor.


