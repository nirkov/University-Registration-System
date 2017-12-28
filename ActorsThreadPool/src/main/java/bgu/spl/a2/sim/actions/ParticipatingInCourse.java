package bgu.spl.a2.sim.actions;


import java.util.List;
import java.util.HashMap;
import java.util.LinkedList;
import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

public class ParticipatingInCourse extends Action<Boolean>{
    private String COURSE;
    private String STUDENT_NAME;
    private String GRADE;


    public ParticipatingInCourse(String Course , String studentID , String grade){
        COURSE = Course;
        STUDENT_NAME = studentID;
        GRADE = grade;
        setActionName("Participate In Course");
    }

    @Override
    protected void start() {//start in course actor
        Action<Boolean> STUDENT_PARTICIPATE = new ParticipatingInCourseConf(COURSE, STUDENT_NAME , GRADE ,(CoursePrivateState) myActorPS );
        List<Action<Boolean>> dependsOnThem = new LinkedList<>();
        dependsOnThem.add(STUDENT_PARTICIPATE);
        sendMessage(STUDENT_PARTICIPATE, STUDENT_NAME, new StudentPrivateState());

        then(dependsOnThem,()->{
            if(STUDENT_PARTICIPATE.getResult().get()) {
                if(((CoursePrivateState) myActorPS).addStudents(STUDENT_NAME)){
                    myActorPS.addRecord(getActionName());
                    ParticipatingInCourseCONF_END PARTICIPATE_END = new ParticipatingInCourseCONF_END( COURSE,STUDENT_NAME ,  GRADE  );
                    sendMessage (PARTICIPATE_END ,STUDENT_NAME , new StudentPrivateState());
                    complete(true);
                }else {
                    ((CoursePrivateState)myActorPS).addRecord(getActionName());
                    complete(false);
                }
            }else{
                complete(false);
                ((CoursePrivateState)myActorPS).addRecord(getActionName());
            }
        });
    }
}






