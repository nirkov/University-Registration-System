package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;

public class AddStudentCONF extends Action<Boolean> {

    public AddStudentCONF() {}

    @Override
    protected void start() {
        complete(true);
        return;
    }
}
