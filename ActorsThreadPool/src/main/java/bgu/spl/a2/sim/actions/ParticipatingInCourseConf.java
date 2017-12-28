package bgu.spl.a2.sim.actions;

import java.util.HashMap;
import java.util.List;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

public class ParticipatingInCourseConf extends Action<Boolean> {

    private String STUDENT_NAME;
    private String GRADE;
    private CoursePrivateState COURSE_PS;
    private String COURSE;



    public ParticipatingInCourseConf(String course, String studentID , String grade , CoursePrivateState coursePS){
        STUDENT_NAME = studentID;
        GRADE = grade;
        COURSE_PS = coursePS;
        COURSE = course;
    }

    @Override
    protected void start() {
        boolean havePrerequisites = true;
        List<String> pre = COURSE_PS.getPrequisites();
        HashMap<String, Integer> grades = ((StudentPrivateState)myActorPS).getGrades();
        for(String s : pre ) {
            if(!grades.containsKey(s)) {
                havePrerequisites = false;
                break;
            }
        }
        if(havePrerequisites && !grades.containsKey(COURSE)) {
            complete(true);
        }else complete(false);
    }
}
