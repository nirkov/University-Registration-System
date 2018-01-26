package bgu.spl.a2;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * an abstract class that represents an action that may be executed using the
 * {@link ActorThreadPool}
 * @param <R> the action result type
 */
public abstract class Action<R> {
    protected Promise<R> myPromise = new Promise<>();
    private String actionName;
    private AtomicBoolean toContinue = new AtomicBoolean(false);
    protected ActorThreadPool myPool;
    private callback continueWithCallback;
    private AtomicInteger numOfActionThisDepend;
    private String actorId;
    protected PrivateState myActorPS;
 
    
    protected abstract void start();


    /**
     * start/continue handling the action
     */
    /*package*/
    final void handle(ActorThreadPool pool, String actorId, PrivateState actorState) {
        if(toContinue.get()) {
            continueWithCallback.call();
        }else {
            myPool = pool;
            myActorPS = actorState;
            this.actorId = actorId;
            this.start();
        }
    }


    /**
     * add a callback to be executed once all the given actions results are
     * resolved
     * @param actions
     * @param callback the callback to execute once all the results are resolved
     */
    protected final void then(Collection<? extends Action<?>> actions, callback callback) {
        numOfActionThisDepend = new AtomicInteger(actions.size());
        for (Iterator <? extends Action<?>> iterator = actions.iterator(); iterator.hasNext();) {   //We'll go through the whole collection
            iterator.next().getResult().subscribe( ()-> {                                           //For each action in the collection we will
                numOfActionThisDepend.decrementAndGet();                                            //insert Promise that when all will receive a resulte,
                if(numOfActionThisDepend.get() == 0) {                                              // we will insert the callback into the current
                    continueWithCallback = callback;                                                //action and send it back to the pool
                    toContinue.set(true);
                    sendMessage(this , actorId , myActorPS);
                }
            });
        }
    }

    /**
     * resolve the internal result - should be called by the action derivative
     * once it is done.
     *
     * @param result - the action calculated result
     */
    protected final void complete(R result) {
        if(!myPromise.isResolved()) {
            myPromise.resolve(result);
        }
    }

    /**
     * @return action's promise (result)
     */
    public final Promise<R> getResult() {
        return myPromise;
    }

    /**
     * send an action to an other actor
     *
     * @param action
     * 				the action
     * @param actorId
     * 				actor's id
     * @param actorState
     * 				actor's private state (actor's information)
     *
     * @return promise that will hold the result of the sent action
     */
    public Promise<?> sendMessage(Action<?> action, String actorId, PrivateState actorState){
        myPool.submit(action, actorId, actorState);
        return this.getResult();
    }
    /**
     * set action's name
     * @param actionName
     */
    public void setActionName(String actionName){
        this.actionName = actionName;
    }

    /**
     * @return action's name
     */
    public String getActionName(){
        return actionName;
    }
}
