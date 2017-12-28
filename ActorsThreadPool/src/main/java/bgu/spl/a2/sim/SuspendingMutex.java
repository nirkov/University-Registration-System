
package bgu.spl.a2.sim;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import bgu.spl.a2.Promise;


/**
 *
 * this class is related to {@link Computer}
 * it indicates if a computer is free or not
 *
 * Note: this class can be implemented without any synchronization.
 * However, using synchronization will be accepted as long as the implementation is blocking free.
 *
 */
public class SuspendingMutex {
	private Computer computer;
	private Queue<Promise<Computer>> promiseQueue;
	private AtomicBoolean FREE;


	/**
	 * Constructor
	 * @param computer
	 */
	//constructor of SuspendingMutex
	public SuspendingMutex(Computer computer){
		this.computer=computer;
		promiseQueue= new LinkedList();
		FREE = new AtomicBoolean(true);

	}
	/**
	 * Computer acquisition procedure
	 * Note that this procedure is non-blocking and should return immediatly
	 *
	 * @return a promise for the requested computer
	 */

	//Must be synchronized, otherwise 2 different Department
	//will be able to get the same computer at the same time.

	public synchronized Promise<Computer>  down(){
		Promise <Computer> newPromise = new Promise<>();
		if(FREE.get()) {
			newPromise.resolve(this.computer);
			FREE.set(false);
		}else promiseQueue.add(newPromise);
		return newPromise;
	}

	/**
	 * Computer return procedure
	 * releases a computer which becomes available in the warehouse upon completion
	 */


	public synchronized void  up(){
		if(promiseQueue.isEmpty())
			FREE.set(true);
		else promiseQueue.poll().resolve(computer);
	}
}


