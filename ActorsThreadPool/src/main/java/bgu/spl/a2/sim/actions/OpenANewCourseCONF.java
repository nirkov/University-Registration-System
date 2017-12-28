package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;

public class OpenANewCourseCONF extends Action<Boolean> {
    private String SPACES;
    private String[] PREREQUISITES;
    private Boolean EXISTS;

    public OpenANewCourseCONF( String spaces , String[] Prerequisites , boolean exists) {
        SPACES = spaces;
        PREREQUISITES = Prerequisites;
        EXISTS = exists;
    }
    @Override
    protected void start() {
        if(!EXISTS){
            ((CoursePrivateState) myActorPS).setSpaces(Integer.parseInt(SPACES));
            ((CoursePrivateState) myActorPS).setPrequisites(PREREQUISITES);
            complete(true);
        }else complete(false);


    }
}
