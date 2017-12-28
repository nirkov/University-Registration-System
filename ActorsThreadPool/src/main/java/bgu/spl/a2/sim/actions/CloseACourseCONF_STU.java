package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

public class CloseACourseCONF_STU extends Action <Boolean>{
    private String COURSE;

    public CloseACourseCONF_STU(String course) {
        COURSE = course;
    }

    @Override
    protected void start() {
        complete(((StudentPrivateState)myActorPS).UnregisterFromCourse(COURSE));
    }
}
