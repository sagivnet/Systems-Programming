package bgu.spl.a2;

import java.util.Vector;

/**
 * this class represents a deferred result i.e., an object that eventually will
 * be resolved to hold a result of some operation, the class allows for getting
 * the result once it is available and registering a callback that will be
 * called once the result is available.
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 *
 * @param <T> the result type, <boolean> resolved - initialized ;
 */
public class Promise<T>{
	
	//Fields
	
	private boolean resolved = false;
	private Vector<callback> dependseOnMe = new Vector<callback>();
	
	private T result;
	
	
	
	//Methods
	/**
	 *
	 * @return the resolved value if such exists (i.e., if this object has been
	 *         {@link #resolve(java.lang.Object)}ed
	 * @throws IllegalStateException
	 *             in the case where this method is called and this object is
	 *             not yet resolved
	 */
	 //This method is synchronized because if isResolved is questioned and gets false,
	 //Another thread comes in and resolves the object than an exception is thrown although it should not.
	public synchronized T get()
	{
		if(!isResolved())
			throw new IllegalStateException("Promise has not resolved yet");
		
		return result;
	}
	
	/**
	 *
	 * @return true if this object has been resolved - i.e., if the method
	 *         {@link #resolve(java.lang.Object)} has been called on this object
	 *         before.
	 */
	public boolean isResolved() { return resolved; }
	
	
	/**
	 * resolve this promise object - from now on, any call to the method
	 * {@link #get()} should return the given value
	 *
	 * Any callbacks that were registered to be notified when this object is
	 * resolved via the {@link #subscribe(callback)} method should
	 * be executed before this method returns
	 *
	 * @throws IllegalStateException
	 * 			in the case where this object is already resolved
	 * @param value - the value to resolve this promise object with
	 */

	//This function is synchronized because if the resolvedValue is set, and another thread calls the isResolved
	//method between the 2 lines - it gets false While the answer is true. Meaning - the invariant is false.
	public synchronized void resolve(T value)
	{
		if(isResolved())
			throw new IllegalStateException("Promise has already resolved");
		
		resolved = true;
		
		result = value;
		
		
		for(int i=0 ; i<dependseOnMe.size () ; i++)
		{
			dependseOnMe.get (i).call ();
			dependseOnMe.remove (i);
		}
		
		
		
		
	}
	
	/**
	 * add a callback to be called when this object is resolved. If while
	 * calling this method the object is already resolved - the callback should
	 * be called immediately
	 *
	 * Note that in any case, the given callback should never get called more
	 * than once, in addition, in order to avoid memory leaks - once the
	 * callback got called, this object should not hold its reference any
	 * longer.
	 *
	 * @param callback
	 *            the callback to be called when the promise object is resolved
	 */

	// This method is synchronized because if isResolved is called, gets false, and then another
	// thread uses "resolve" the callback will never run.
	public synchronized void subscribe(callback callback)
	{
		
		if(isResolved())
		{
			callback.call ();
			dependseOnMe.remove (callback);
		}
		
		else {dependseOnMe.add(callback);}
		
	}
}
