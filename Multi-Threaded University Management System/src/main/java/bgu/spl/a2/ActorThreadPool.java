package bgu.spl.a2;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * represents an actor thread pool - to understand what this class does please
 * refer to your assignment.
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 */
public class ActorThreadPool {
	
	//Fields
	
	private Map<String, Queue<Action>> actorsQueues;
	private Map<String, PrivateState> actorsPrivateStates;
	private List<String> actorsIds;
	
	public Map<String, AtomicBoolean> actorsLocks;
	
	private int nthreads;
	public VersionMonitor monitor;
	private Vector <Thread> threads;
	
	
	
	
	//Methods
	/**
	 * creates a {@link ActorThreadPool} which has nthreads. Note, threads
	 * should not get started until calling to the {@link #start()} method.
	 *
	 * Implementors note: you may not add other constructors to this class nor
	 * you allowed to add any other parameter to this constructor - changing
	 * this may cause automatic tests to fail..
	 *
	 * @param nthreads
	 *            the number of threads that should be started by this thread
	 *            pool
	 */
	public ActorThreadPool(int nthreads) {
		
		ActorThreadPool pool = this;
		
		this.nthreads = nthreads;
		monitor = new VersionMonitor ();
		
		//Lists Creation
		actorsQueues = new ConcurrentHashMap<String, Queue<Action>> ();
		actorsIds = new Vector<String>();
		actorsPrivateStates = new ConcurrentHashMap<String, PrivateState> ();
		actorsLocks = new ConcurrentHashMap<String, AtomicBoolean>();
		threads = new Vector<Thread>();
		
		//Threads Creation
		for (int i = 0; i < nthreads;i++)
		{
			threads.add (new Thread (new Runnable()
			{
				//Fields
				Action action;
				String actor;
				//Method
				public void run ()
				{
					while(!Thread.currentThread ().isInterrupted ())
					{
						final int ver = monitor.getVersion ();
						
						try {
							for (String actorName : actorsIds) {
								
								Queue currQueue = actorsQueues.get (actorName);
								
								actor = actorName;
								if (actorsLocks.get (actorName).compareAndSet (false, true)) {
									
									action = (Action) currQueue.poll ();
									
									if (action != null)
										action.handle (pool, actorName, getActorPrivateState (actorName));
									
									//Release lock
									actorsLocks.get (actorName).set (false);

								}
							}
						}catch (ConcurrentModificationException e) {}
		
						if(monitor.getVersion ()==ver)
							try	{monitor.await (ver);}

					//		Thread was Interrupted and should finish
							catch (InterruptedException e)
							{
//								try{Thread.currentThread (). ();}
//								catch ( ex){}
								return;
							}

					}
				}
			}));
		}
	}
	
	/**
	 * submits an action into an actor to be executed by a thread belongs to
	 * this thread pool
	 *
	 * @param action
	 *            the action to execute
	 * @param actorId
	 *            corresponding actor's id
	 * @param actorState
	 *            actor's private state (actor's information)
	 */
	public void submit(Action<?> action, String actorId, PrivateState actorState)
	{
		if(!actorsIds.contains(actorId))
		{
			//New actor creation
			actorsLocks.put (actorId,new AtomicBoolean (true));
			actorsPrivateStates.put(actorId , actorState);
			actorsIds.add(actorId);
			
			Queue actorQueue = new ConcurrentLinkedQueue();
			
			if(action != null)
				actorQueue.add(action);
			
			actorsQueues.put(actorId , actorQueue);
			actorsLocks.get (actorId).set (false);
		}
		
		else
		{
			//Busy wait until actor is available
			while(!actorsLocks.get(actorId).compareAndSet(false,true)) {
				try{Thread.sleep(10);}catch (InterruptedException e){Thread.currentThread ().interrupt ();}
			}
			actorsQueues.get(actorId).add(action);
			
			//Release lock
			actorsLocks.get(actorId).set(false);
		}
		monitor.inc ();
	}
	
	/**
	 * closes the thread pool - this method interrupts all the threads and waits
	 * for them to stop - it is returns *only* when there are no live threads in
	 * the queue.
	 *
	 * after calling this method - one should not use the queue anymore.
	 *
	 * @throws InterruptedException
	 *             if the thread that shut down the threads is interrupted
	 */
	public void shutdown() throws InterruptedException
	{
		
		for (Thread thread : threads)
		{
			if (Thread.interrupted ())
				throw new InterruptedException ("ActorThreadPool::shutdown() has Interrupted");

			thread.interrupt ();
		}
		
		
		//wait for all the threads to finish
		for (Thread thread : threads)
			while (thread.isAlive())
				Thread.sleep(10);
	}
	
	/**
	 * start the threads belongs to this thread pool
	 */
	public void start()
	{
		for(Thread thread: threads)
			thread.start ();
	}
	
	public PrivateState getActorPrivateState(String name) { return actorsPrivateStates.get(name); }

	public Map<String, PrivateState> getStatesMap(){return actorsPrivateStates;}
	
	public void submitMyself (Action action , String actorID)
	{
		actorsQueues.get(actorID).add(action);
	}

}