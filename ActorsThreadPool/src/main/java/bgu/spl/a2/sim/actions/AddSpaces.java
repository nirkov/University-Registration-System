package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;

public class AddSpaces extends Action<Boolean>{
    private String COURSE_NAME;
    private int NUM_TO_ADD;

    public AddSpaces(String courseName , String numberOfSpaces) {
        COURSE_NAME = courseName;
        NUM_TO_ADD = Integer.parseInt(numberOfSpaces);
        setActionName("Add Spaces");
    }

    @Override
    protected void start() { //start in course actor
        if(NUM_TO_ADD > 0) {
            ((CoursePrivateState) myActorPS).addSpaces(NUM_TO_ADD);
            complete(true);
            myActorPS.addRecord(getActionName());
        }else complete(false);
    }
}
