package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

public class ParticipatingInCourseCONF_END extends Action<Boolean> {
    private String COURSE;
    private String STUDENT_NAME;
    private String GRADE;

    public ParticipatingInCourseCONF_END(String course, String studentID , String grade ){
        COURSE =course;
        STUDENT_NAME =studentID;
        GRADE =grade;
    }

    @Override
    protected void start(){
        ((StudentPrivateState)myActorPS).addGrade(COURSE, GRADE);
        complete(true);
    }
}
