package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.Computer;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.LinkedList;

public class CheckAdminObliCONF extends Action<Boolean> {
    private LinkedList<String> CONDITION;
    private Computer COMPUTER;

    public CheckAdminObliCONF(LinkedList<String> condition , Computer computer){
        CONDITION = new LinkedList<>();
        CONDITION = condition;
        COMPUTER = computer;
    }

    @Override
    protected void start() {
        ((StudentPrivateState)myActorPS).setSignature(COMPUTER.checkAndSign(CONDITION,
                ((StudentPrivateState) myActorPS).getGrades()));
        complete(true);
    }
}
