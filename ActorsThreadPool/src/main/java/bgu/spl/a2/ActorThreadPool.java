package bgu.spl.a2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;


public class ActorThreadPool {
	private ConcurrentHashMap<String , ConcurrentLinkedQueue<Action>>   mapOfActionQ;          //each cell is an actor implemented as Queue
	private ConcurrentHashMap<String , PrivateState>                    mapOfPS;		   //each cell is an private state
	private Queue<String> 					            actorWaitingToExecute; //list of actor's name of not empty actors
	private ArrayList<Thread> 		                            threadPool;		   //list of Threda
	private ConcurrentHashMap<String , Boolean>			    mapLockedActor;        //each cell is a boolean flag is actor locked: true=locked
	private VersionMonitor 						    VM;
	private AtomicBoolean 						    SHUTDOWN;

	
	public ActorThreadPool(int nthreads) {
		SHUTDOWN = new AtomicBoolean(false);
		mapOfActionQ = new ConcurrentHashMap<>();		//create map of action queue which each queue represent Actor
		mapOfPS = new ConcurrentHashMap<>() ;			//create map of private state of actor
		actorWaitingToExecute = new LinkedList<>();		//list of unlocked Actors
		mapLockedActor = new  ConcurrentHashMap<>();		//Map for idicate which Actor is locked
		createThreadPool(nthreads);
		VM = new VersionMonitor();
	}

	
	public synchronized void submit(Action<?> action, String actorId, PrivateState actorState) {
		ConcurrentLinkedQueue<Action> newActorQ = mapOfActionQ.get(actorId);
		if(newActorQ == null) {					//if this action not exists in the table we create a new action's queue -
			newActorQ = new ConcurrentLinkedQueue<>();	//create new actor implement as queue
			newActorQ.add(action);				//add the recived action to the actor that we created
			mapOfActionQ.put(actorId, newActorQ);		//mapped the new Actor to hashmap of actors - mapped by actorId
			setPrivateState(actorId, actorState);		//mapped the private state of this actor - mapped by actorId
			actorWaitingToExecute.add(actorId);		//we must add this actor to waiting list in this case because this is a new actor
			mapLockedActor.put(actorId , false);
		}else{
			newActorQ.add(action);
			addToWaitingList(actorWaitingToExecute , actorId );
		}
		VM.inc();
	}

	
	public void shutdown() throws InterruptedException {
		SHUTDOWN.set(true);
		VM.inc();
		for(Thread T : threadPool){
			VM.inc();
			T.interrupt();
		}
		for(Thread T : threadPool) T.join();
	}	//Interupt all the threads and join them.

	/**
	 * start the threads belongs to this thread pool
	 */
	public void start() {
		int size = threadPool.size();
		for(int i = 0 ; i < size ; i++) threadPool.get(i).start();
	}									//Start the threads


//******************************************************************************************************
//						FUNCTIONS
//******************************************************************************************************

	private void addToWaitingList(Queue<String> addto ,String actorId ) {
		if(!addto.contains(actorId)) {
			addto.add(actorId);
		}
	}

	private void createThreadPool(int nthreads){
		threadPool = new ArrayList<>();
		for(int i = 0 ; i < nthreads ; i++) {
		    Runnable task = () -> {													//Runnable task as a Lambda function for each thread to execute
			while(!SHUTDOWN.get()) {
			    String nextActorID = nextUnlockedActor();		//the next actor with action to execute
			    if(nextActorID != null) {
				Action toExecute = mapOfActionQ.get(nextActorID).poll();       //take action to execute
				toExecute.handle(this, nextActorID, getPrivaetState(nextActorID));
				endOfMission( nextActorID);
			    }else {
				if(!SHUTDOWN.get()){
				    try {
					VM.await(VM.getVersion()+1);
				    } catch (InterruptedException e) {
					e.printStackTrace();
				    }
				}
			    }
			}
			VM.inc();
			};
			threadPool.add(new Thread(task));
		}
	}


	// The nextUnlockedActor function must be synchronized so that
	// both threads can not simultaneously access to the same ACTOR.
	// The function works so that every time a single thread comes
	// in, finds a free ACTOR and locks it from other threads

	private synchronized String nextUnlockedActor() {
	    String nextUnlocked = null;
	    if(!actorWaitingToExecute.isEmpty()) {
		int size = actorWaitingToExecute.size();
		boolean found = false;
		while(size != 0 && !found) {
	            nextUnlocked = actorWaitingToExecute.peek();											//take peek of waiting actors
    		    if(!mapLockedActor.get(nextUnlocked) && !mapOfActionQ.get(nextUnlocked).isEmpty()){		//if not locked and not empty we return this actor's name
			found = true;
			nextUnlocked = actorWaitingToExecute.peek();
			mapLockedActor.put(nextUnlocked , true);	 //this thread lock this actor from other threads
		    }else {						 //else we want to delete this actor if he empty
			size--;                                          //or insert him in the end of the queue if he locked
			nextUnlocked =  actorWaitingToExecute.poll();
			if(!mapOfActionQ.get(nextUnlocked).isEmpty()) {
		   	    actorWaitingToExecute.add(nextUnlocked);
			}
			nextUnlocked = null;
		    }
		}
	    }
	    return nextUnlocked;
	}

	private void endOfMission(String nextActorID) {	
		mapLockedActor.put(nextActorID, false);
		VM.inc();
	}

	private void setPrivateState(String actorID , PrivateState p) {
		mapOfPS.put(actorID , p );
	}

	public PrivateState getPrivaetState(String actorId) {
		return mapOfPS.get(actorId);
	}

	public  HashMap<String,PrivateState> map_of_private_State() {
		return new HashMap<>(mapOfPS) ;
	}
}













