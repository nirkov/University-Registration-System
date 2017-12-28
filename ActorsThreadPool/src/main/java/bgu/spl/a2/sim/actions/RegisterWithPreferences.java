package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class RegisterWithPreferences extends Action<Boolean> {
    private String STUDENT_NAME;
    private Queue<String> PREFERENCES;
    private Queue<String> GRADE;
    private Queue<Action<Boolean>> TRY;

    public RegisterWithPreferences(String studentID, String[] preferences , String[] grade) {
        if(preferences != null && grade != null){
            if(preferences.length == grade.length){
                STUDENT_NAME = studentID;
                GRADE = new LinkedList<>();
                TRY = new LinkedList<>();
                PREFERENCES = new LinkedList<>();
                setActionName("Register With Preferences");
                for(int i = 0 ; i < preferences.length ; i++){
                        TRY.add(new ParticipatingInCourse( preferences[i] , studentID ,  grade[i]));
                        PREFERENCES.add(preferences[i]);
                        GRADE.add(grade[i]);
                }
            }
        }
    }

    @Override
    protected void start() {	//start from student
        if(PREFERENCES != null && GRADE != null){
            if(!PREFERENCES.isEmpty() && !GRADE.isEmpty()) {
                GRADE.poll();
                List<Action<Boolean>> actions = new LinkedList<>();
                actions.add(TRY.peek());
                sendMessage(TRY.poll(), PREFERENCES.peek(), new CoursePrivateState() );
                then(actions , ()->{
                    if(((StudentPrivateState)myActorPS).isRegisterTo(PREFERENCES.poll())){
                        complete(true);
                        ((StudentPrivateState)myActorPS).addRecord(getActionName());
                    }else if(!TRY.isEmpty()){
                        start();
                    }else{
                        complete(false);
                        ((StudentPrivateState)myActorPS).addRecord(getActionName());
                    }
                });
            }else{
                ((StudentPrivateState)myActorPS).addRecord(getActionName());
                complete(false);
            }
        }else{
            ((StudentPrivateState)myActorPS).addRecord(getActionName());
            complete(false);
        }
    }
}
