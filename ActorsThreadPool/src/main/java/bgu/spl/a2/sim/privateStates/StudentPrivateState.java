package bgu.spl.a2.sim.privateStates;

import java.util.HashMap;

import bgu.spl.a2.PrivateState;

/**
 * this class describe student private state
 */
public class StudentPrivateState extends PrivateState{

	private HashMap<String, Integer> grades;
	private long signature;

	public StudentPrivateState() {
		grades = new HashMap<String, Integer>();
		signature = 0;
	}

	public HashMap<String, Integer> getGrades() {
		return grades;
	}

	public long getSignature() {
		return signature;
	}

	public void setSignature(long signature) {
		this.signature=signature;
	}

	public void addGrade(String course ,String grade) {
		if(grade.compareTo("-") == 0){
			grades.put(course, -1);
		}else if(Integer.parseInt(grade)>=0){
			grades.put(course,  Integer.parseInt(grade));
		}

	}

	public boolean UnregisterFromCourse(String course) {
		boolean Unregister = false;
		if(grades.containsKey(course)) {
			grades.remove(course);
			Unregister = true;
		}
		return Unregister;
	}

	public boolean isRegisterTo(String course){
		return grades.containsKey(course);
	}
}


