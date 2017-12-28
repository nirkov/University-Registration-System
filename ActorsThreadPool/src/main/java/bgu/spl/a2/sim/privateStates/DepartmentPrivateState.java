package bgu.spl.a2.sim.privateStates;

import java.util.LinkedList;
import java.util.List;

import bgu.spl.a2.PrivateState;

/**
 * this class describe department's private state
 */
public class DepartmentPrivateState extends PrivateState{
	private List<String> courseList;
	private List<String> studentList;

	/**
	 * Implementors note: you may not add other constructors to this class nor
	 * you allowed to add any other parameter to this constructor - changing
	 * this may cause automatic tests to fail..
	 */
	public DepartmentPrivateState() {
		courseList= new LinkedList();
		studentList=new LinkedList();
	}

	public List<String> getCourseList() {
		return courseList;
	}

	public List<String> getStudentList() {
		return studentList;
	}

	public void addCourseToDepatment(String newCourse){
		courseList.add(newCourse);
	}

	public void addStudentToDepartment(String newStudent){
		studentList.add(newStudent);
	}

	public Boolean closeCourse(String course) {
		boolean close = false;
		int index = courseList.indexOf(course);
		if(index != -1) {
			courseList.remove(index);
			close = true;
		}
		return close;
	}

	public boolean isContainCourse(String course){
		return courseList.contains(course);
	}

	public boolean isContainStudent(String student){
		return studentList.contains(student);
	}

}