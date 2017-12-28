package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

public class AddStudent extends Action<Boolean> {
    private String DEPARTMENT;
    private String STUDENT_NAME;

    public AddStudent(String department, String studentID) {
        DEPARTMENT = department;
        STUDENT_NAME = studentID;
        setActionName("Add Student");
    }

    @Override
    protected void start() {	//from department
        Action<Boolean> STUDENT_ADD_STUDENT = new AddStudentCONF();						//AddStudentCONF is empty start action
        sendMessage(STUDENT_ADD_STUDENT, STUDENT_NAME, new StudentPrivateState());		//open actor of student with empty action
        ((DepartmentPrivateState) myActorPS).addStudentToDepartment(STUDENT_NAME);		//add student to department
        complete(true);																	//resolve the added with ture
        ((DepartmentPrivateState) myActorPS).addRecord(getActionName());				//add the command to department's history
    }
}
