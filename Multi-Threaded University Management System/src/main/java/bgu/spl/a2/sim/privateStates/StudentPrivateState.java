package bgu.spl.a2.sim.privateStates;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bgu.spl.a2.PrivateState;

/**
 * this class describe student private state
 */
public class StudentPrivateState extends PrivateState {
	
	private HashMap<String, String> grades;
	private long signature;
	
	/**
	 * Implementors note: you may not add other constructors to this class nor
	 * you allowed to add any other parameter to this constructor - changing
	 * this may cause automatic tests to fail..
	 */
	public StudentPrivateState() { grades = new HashMap<String, String> (); }
	
	public HashMap<String, String> getGrades() {
		return grades;
	}
	
	public long getSignature() {
		return signature;
	}
	
	public void addGrades(Map<String,String> gradesToAdd) { grades.putAll (gradesToAdd); }
	
	public void addGrade(String course, String grade) {
		grades.put (course,grade); }
	
	public void setSignature(long signature) {this.signature=signature;}
	
	public boolean canParticipate(List<String> prerequisites)
	{
		for (String course : prerequisites)
			if ( ! grades.containsKey (course))
				return false;
		
		return true;
	}
	public boolean unregister(String course) {
		if (grades.remove (course) != null)
			return true;
		return false;
		
	}
	
}