package bgu.spl.a2.sim.actions;

import java.util.LinkedList;
import java.util.List;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

public class Unregister extends Action<Boolean>{
    private String STUDENT_NAME;
    private String COURSE;

    public Unregister(String studentID , String course){
        STUDENT_NAME = studentID;
        COURSE = course;
        setActionName("Unregister");
    }

    protected void start() {// start from course actor
        List<Action<Boolean>> actions = new LinkedList();
        Action<Boolean> STUDENT_UNREGISTER = new UnregisterCONF(COURSE);
        actions.add(STUDENT_UNREGISTER);
        sendMessage(STUDENT_UNREGISTER, STUDENT_NAME, new StudentPrivateState());

        then(actions, ()->{
            if(((CoursePrivateState)myActorPS).unregisterStudent(STUDENT_NAME)) {
                complete(true);
                ((CoursePrivateState) myActorPS).addRecord(getActionName());
            }else{
                complete(false);
                ((CoursePrivateState) myActorPS).addRecord(getActionName());
            }
        });
    }
}
