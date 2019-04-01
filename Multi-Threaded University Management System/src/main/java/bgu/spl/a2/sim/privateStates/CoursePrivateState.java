package bgu.spl.a2.sim.privateStates;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import bgu.spl.a2.PrivateState;

/**
 * this class describe course's private state
 */
public class CoursePrivateState extends PrivateState {
	private AtomicBoolean lock;
	private Integer availableSpots;	// We get from outside
	private Integer registered;
	private List<String> regStudents;
	private List<String> prerequisites; // We get from outside
	
	/**
	 * Implementors note: you may not add other constructors to this class nor
	 * you allowed to add any other parameter to this constructor - changing
	 * this may cause automatic tests to fail..
	 */
	public CoursePrivateState()
	{
		availableSpots = new Integer (0);
		registered = new Integer(0);
		regStudents = new ArrayList<String>();
		lock = new AtomicBoolean (false);
		//availableSpots = new Integer (0);
	}
	
	public Integer getAvailableSpots() { return availableSpots; }
	
	public Integer getRegistered() {
		return registered;
	}
	
	public List<String> getRegStudents() {
		return regStudents;
	}
	
	public List<String> getPrerequisites() {
		return prerequisites;
	}
	
	public void setAvailableSpots(Integer spots) { availableSpots = spots ;}
	
	public void saveSpot() { availableSpots -- ;}
	
	public void unsaveSpot() { availableSpots ++ ;}
	
	public void addToAvailableSpots(Integer spots) { availableSpots += spots ;}
	
	public void setPrerequisites (List<String> prerequisites) { this.prerequisites = prerequisites; }
	
	public void register(String student)
	{
		registered++;
		regStudents.add(student);
	}
	
	
	public boolean unregister(String student)
	{
		
		boolean succeed = regStudents.remove (student);
		if(succeed)
		{
			registered--;
			availableSpots++;
		}
		
		return succeed;
	}
	
}