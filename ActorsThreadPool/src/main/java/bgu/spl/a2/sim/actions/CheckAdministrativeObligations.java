package bgu.spl.a2.sim.actions;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.callback;
import bgu.spl.a2.sim.Computer;
import bgu.spl.a2.sim.Warehouse;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;


public class CheckAdministrativeObligations extends Action<Boolean> {
    private Warehouse WAREHOUSE;
    private Queue<String> STUDENTS;
    private String COMPUTER_ASK;
    private LinkedList<String> CONDITIONS;
    private boolean firstAsk;
    private Computer COMPUTER;

    public CheckAdministrativeObligations (Warehouse warehouse,String[] students,String computer,LinkedList<String> conditions ){
        WAREHOUSE=warehouse;
        COMPUTER_ASK=computer;
        CONDITIONS=conditions;
        STUDENTS = new LinkedList<>();
        setActionName("Administrative Check");
        for(String s : students) STUDENTS.add(s);
        firstAsk = true;
    }

    @Override
    protected void start() {
        List<Action<Boolean>> actions = new LinkedList<>();
        if(firstAsk){
            firstAsk = false;
            Promise<Computer> askforComputerP = WAREHOUSE.askForComputer(COMPUTER_ASK);
            askforComputerP.subscribe(()->{
                COMPUTER = askforComputerP.get();
                CheckAdminObliCONF checkStudent = new CheckAdminObliCONF(CONDITIONS,COMPUTER);
                actions.add(checkStudent);
                sendMessage(checkStudent ,STUDENTS.poll(),new StudentPrivateState());
                then(actions , ()->{
                    if(!STUDENTS.isEmpty()){
                        start();
                    }else{
                        complete(true);
                        WAREHOUSE.releaseComputer(COMPUTER_ASK);
                    }
                });
            });
        }else{
            CheckAdminObliCONF checkStudent = new CheckAdminObliCONF(CONDITIONS,COMPUTER);
            actions.add(checkStudent);
            sendMessage(checkStudent ,STUDENTS.poll(),new StudentPrivateState());
            then(actions , ()->{
                if(!STUDENTS.isEmpty()){
                    start();
                }else{
                    complete(true);
                    WAREHOUSE.releaseComputer(COMPUTER_ASK);
                    myActorPS.addRecord(getActionName());
                }
            });
        }
    }
}


