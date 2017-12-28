package bgu.spl.a2.sim.privateStates;

import java.util.LinkedList;
import java.util.List;

import bgu.spl.a2.PrivateState;

/**
 * this class describe course's private state
 */
public class CoursePrivateState extends PrivateState{

	private Integer availableSpots;
	private Integer registered;
	private List<String> regStudents;
	private List<String> prequisites;

	/**
	 * Implementors note: you may not add other constructors to this class nor
	 * you allowed to add any other parameter to this constructor - changing
	 * this may cause automatic tests to fail..
	 */
	public CoursePrivateState() {
		availableSpots = 0;
		registered = 0;
		regStudents = new LinkedList();
		prequisites = new LinkedList();
	}

	public  Integer getAvailableSpots() {
		return availableSpots;
	}

	public Integer getRegistered() {
		return registered;
	}

	public List<String> getRegStudents() {
		return regStudents;
	}

	public List<String> getPrequisites() {
		return prequisites;
	}

	public boolean addStudents(String studentID) {
		boolean isAdd = false;
		if(availableSpots > 0 && (!regStudents.contains(studentID))) {
			regStudents.add(studentID);
			availableSpots --;
			registered ++;
			isAdd = true;
		}
		return isAdd;
	}

	public boolean unregisterStudent(String studentID) {
		boolean unregister = false;
		if(availableSpots > -1 && !regStudents.isEmpty()){
			int index = regStudents.indexOf(studentID);
			if(index != -1) {
				regStudents.remove(index);
				availableSpots ++;
				registered --;
				unregister = true;
			}
		}
		return unregister;
	}

	public void setSpaces(int spaces) {
		if(availableSpots > -1) {
			availableSpots = spaces;
		}
	}

	public void setPrequisites(String[] Prequisites ) {
		for(String s :Prequisites ) prequisites.add(s);
	}

	public void addSpaces(int numToAdd) {
		if(availableSpots > -1) {
			availableSpots = availableSpots + numToAdd;
		}
	}

	public void closeMe(){
		availableSpots = -1;
		registered = 0;
		regStudents.clear();

	}
}
