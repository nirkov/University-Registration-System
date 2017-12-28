package bgu.spl.a2.sim.actions;

import java.util.LinkedList;
import java.util.List;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

public class CloseACourseCONF_COU extends Action<Boolean>{
    private String COURSE;


    public CloseACourseCONF_COU(String course ) {
        COURSE = course;
    }

    @Override
    protected void start() {
        List<String> STUDENTS_NAMES =  ((CoursePrivateState) myActorPS).getRegStudents();
        ((CoursePrivateState) myActorPS).closeMe();
        if(!STUDENTS_NAMES.isEmpty()){
            List<Action<Boolean>> STUDENT_IN_DEPARTMENT = new LinkedList<>();
            Action<Boolean> STUDENT_CLOSE_COURSE;
            for(String student : STUDENTS_NAMES) {
                STUDENT_CLOSE_COURSE = new CloseACourseCONF_STU(COURSE);
                STUDENT_IN_DEPARTMENT.add(STUDENT_CLOSE_COURSE);
                sendMessage(STUDENT_CLOSE_COURSE, student, new StudentPrivateState());
            }

            then(STUDENT_IN_DEPARTMENT,()->{
                ((CoursePrivateState) myActorPS).closeMe();
                complete(true);
            });
        }else{
            complete(true);
            ((CoursePrivateState) myActorPS).closeMe();
        }

    }
}
