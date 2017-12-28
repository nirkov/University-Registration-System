package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

public class UnregisterCONF extends Action<Boolean>{
    private String COURSE;

    public UnregisterCONF(String course){
        COURSE = course;
    }

    @Override
    protected void start() {
        if(((StudentPrivateState)myActorPS).UnregisterFromCourse(COURSE)) {
            complete(true);
        }else{
            complete(false);
        }
    }
}
