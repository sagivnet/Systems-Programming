package bgu.spl.a2.sim;
import bgu.spl.a2.Promise;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 
 * this class is related to {@link Computer}
 * it indicates if a computer is free or not
 * 
 * Note: this class can be implemented without any synchronization. 
 * However, using synchronization will be accepted as long as the implementation is blocking free.
 *
 */
public class SuspendingMutex
{
	//	Fields
	private AtomicBoolean inUseOfDepartment;
	private Computer computer;
	private Queue<Promise<Computer>> departmentsQueue;
	
	//	Methods
	public SuspendingMutex(Computer computer)
	{
		inUseOfDepartment = new AtomicBoolean (false);
		this.computer = computer;
		departmentsQueue = new ConcurrentLinkedQueue<>();
	}

	/**
	 * Computer acquisition procedure
	 * Note that this procedure is non-blocking and should return immediatly
	 *
	 * @return a promise for the requested computer
	 */
	

	public Promise<Computer> down()
	{
        Promise<Computer>  promise = new Promise<Computer>();

		//Computer is Not locked
		if	(inUseOfDepartment.compareAndSet (false,true))
			promise.resolve(computer);

		else
			departmentsQueue.add(promise);
		
		return promise;
	}




	/**
	 * Computer return procedure
	 * releases a computer which becomes available in the warehouse upon completion
	 */
	public void up()
	{
		Promise currPromise = departmentsQueue.poll();
		if (currPromise == null)
			inUseOfDepartment.set(false);

		else
			currPromise.resolve(computer);
	}




}
