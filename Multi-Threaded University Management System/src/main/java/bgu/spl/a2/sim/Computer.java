package bgu.spl.a2.sim;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;


import com.google.gson.annotations.SerializedName;

public class Computer {

	//	Fields
	private String Type;
	@SerializedName("Sig Fail")
	private long failSig;
	@SerializedName("Sig Success")
	private long successSig;

	private AtomicBoolean lock = new AtomicBoolean(false);
	
	//	Methods
	/**
	 * this method checks if the courses' grades fulfill the conditions
	 * @param courses
	 * 							courses that should be pass
	 * @param coursesGrades
	 * 							courses' grade
	 * @return a signature if couersesGrades grades meet the conditions
	 */
	
	public long checkAndSign(List<String> courses, Map<String, String> coursesGrades)
	{
		for(String course : courses) {
			String grade = coursesGrades.get (course);
			if (grade == null || grade.equals("-") || Integer.parseInt (grade) < 56)
				return failSig;
			
		}
		return successSig;
	}

	public String getType() {
		return Type;
	}

	public boolean lockPC()	{return lock.compareAndSet(false,true);}
	public void unlockPC() {lock.set(false);}
}
