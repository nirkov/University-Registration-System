package bgu.spl.a2.sim.actions;

import java.util.LinkedList;
import java.util.List;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

public class CloseACourse extends Action<Boolean>{
    private String COURSE;
    private String DEPARTMENT;

    public CloseACourse(String course, String department) {
        COURSE = course;
        DEPARTMENT = department;
        setActionName("Close Course");
    }

    @Override
    protected void start() {	//start from department
        if(((DepartmentPrivateState)myActorPS).isContainCourse(COURSE)){
            Action<Boolean> COURSE_CLOSE_COURSE = new  CloseACourseCONF_COU(COURSE);	//action for the course we want close
            List<Action<Boolean>> actions = new LinkedList<>();										//list for then with action-course
            actions.add(COURSE_CLOSE_COURSE);
            sendMessage(COURSE_CLOSE_COURSE, COURSE, new CoursePrivateState());						//send the action to the course in threadpool

            then(actions, ()->{
                if(((DepartmentPrivateState)myActorPS).closeCourse(COURSE)) {                            //delete the course from the dipartment list of courses
                    complete(true);
                    ((DepartmentPrivateState) myActorPS).addRecord(getActionName());
                }else{
                    complete(false);
                    ((DepartmentPrivateState) myActorPS).addRecord(getActionName());

                }
            });
        }else{
            complete(false);
            ((DepartmentPrivateState) myActorPS).addRecord(getActionName());
        }

    }
}
