package bgu.spl.a2.sim.actions;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

public class OpenANewCourse extends Action<Boolean> {
    private String DEPARTMENT;
    private String COURSE_NAME;
    private String SPACES;
    private String[] PREREQUISITES;
    private boolean reciverSide;


    public OpenANewCourse(String department, String courseName , String spaces , String[] Prerequisites) {
        reciverSide = false;
        DEPARTMENT = department;
        COURSE_NAME = courseName;
        SPACES = spaces;
        PREREQUISITES = Prerequisites;
        setActionName("Open Course");
    }

    @Override
    protected void start() {	//start from department
        boolean exists = ((DepartmentPrivateState) myActorPS).isContainCourse(COURSE_NAME);
        Action<Boolean> COURSE_OPEN_COURSE = new OpenANewCourseCONF(SPACES ,PREREQUISITES, exists);
        List<Action<Boolean>> actions = new LinkedList<>();
        actions.add(COURSE_OPEN_COURSE);											//Collection for then
        sendMessage(COURSE_OPEN_COURSE, COURSE_NAME, new CoursePrivateState());		//open actor for this course

        then(actions, ()->{															//add the course to department after the course was open
           if(!((DepartmentPrivateState) myActorPS).isContainCourse(COURSE_NAME)){
               ((DepartmentPrivateState) myActorPS).addCourseToDepatment(COURSE_NAME);
               complete(true);
               ((DepartmentPrivateState) myActorPS).addRecord(getActionName());
            }else{
               complete(false);
               ((DepartmentPrivateState) myActorPS).addRecord(getActionName());
           }
        });
    }
}


