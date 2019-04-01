package bgu.spl.a2;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * an abstract class that represents an action that may be executed using the
 * {@link ActorThreadPool}
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add to this class can
 * only be private!!!
 *
 * @param <R> the action result type
 */
public abstract class Action<R>
{
    //----------------------------------------Fields----------------------------------------
    //--------------------------------------------------------------------------------------
    //My Fields
    protected String actionName;
    protected Promise <R> promise = new Promise<> ();
    protected callback continueProgress =null;
    
    //My Owners
    protected ActorThreadPool pool;
    protected String actorId;
    protected PrivateState actorState;
    
    //For the actions I depend on to use
    protected AtomicInteger actionsIDepOnCounter;
    
    final Action me = this;

    
    //----------------------------------------Methods------------------------------------------
    //-----------------------------------------------------------------------------------------
    /**
     * start handling the action - note that this method is protected, a thread
     * cannot call it directly.
     */
    
    
    protected abstract void start();
    
    
    //----------------------------------------------------------------------------------
    /**
     *
     * start/continue handling the action
     *
     * this method should be called in order to start this action
     * or continue its execution in the case where it has been already started.
     *
     * IMPORTANT: this method is package protected, i.e., only classes inside
     * the same package can access it - you should *not* change it to
     * public/private/protected
     *
     */
    
    
    /*package*/ final void handle(ActorThreadPool pool, String actorId, PrivateState actorState)
    {
        
        if(continueProgress == null) // it's the first time
        {
            this.actorState = actorState;
            this.actorId = actorId;
            this.pool = pool;
            
            start();
        }
        else
            continueProgress.call();
    }//------------------------------------------------------------------------------------
    /**
     * add a callback to be executed once *all* the given actions results are
     * resolved
     *
     * Implementors note: make sure that the callback is running only once when
     * all the given actions completed.
     *
     * @param actions
     * @param callback the callback to execute once all the results are resolved
     */
    
    
    //called from start()
    
    protected final void then(Collection<? extends Action<?>> actions, callback callback)
    {
        continueProgress = callback;
        
        actionsIDepOnCounter = new AtomicInteger (actions.size());
        callback subActionCallback = ()->
            {
                actionsIDepOnCounter.decrementAndGet ();
                
                //All the actions I depends on had finished, get me back to the queue
                if (actionsIDepOnCounter.get () == 0)
                    pool.submit(me,actorId,actorState);
                    
            };
        
        if (actions.isEmpty())
            continueProgress.call();

            for (Action action : actions) {
                action.getResult().subscribe (subActionCallback);
        }
    }//-------------------------------------------------------------------------------------
    /**
     * resolve the internal result - should be called by the action derivative
     * once it is done.
     *
     * @param result - the action calculated result
     */
    
    //called from continueProgress callback
    
    protected final void complete(R result)
    {
        
        actorState.addRecord(actionName);
        promise.resolve(result);
//        pool.monitor.inc();

    }
    //---------------------------------------------------------------------------------------
    /**
     * @return action's promise (result)
     */
    
    
    public final Promise<R> getResult() { return promise; }
    //---------------------------------------------------------------------------------------------
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
    
    //called from start()
    
    
    public Promise<?> sendMessage(Action<?> action, String actorId, PrivateState actorState)
    {
        pool.submit(action,actorId,actorState);
        return action.promise;
    }
    //------------------------------------------------------------------------------------=
    /**
     * set action's name
     * @param actionName
     */
    
    
    public void setActionName(String actionName){actionName = actionName;}
    //----------------------------------------------------------------------------------------
    /**
     * @return action's name
     */
    
    
    public String getActionName(){return actionName;}
}//----------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------