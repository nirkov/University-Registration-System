package bgu.spl.a2.sim;

import java.util.HashMap;
import java.util.List;

import bgu.spl.a2.Promise;

/**
 * represents a warehouse that holds a finite amount of computers
 * and their suspended mutexes.
 * releasing and acquiring should be blocking free.
 */

public class Warehouse {
    HashMap < String, SuspendingMutex> MUTEX;

    public Warehouse(HashMap < String, SuspendingMutex> computers) {
        MUTEX = computers;
    }

    public Promise<Computer> askForComputer(String computer) {
        return MUTEX.get(computer).down();
    }

    public void releaseComputer(String computer) {
        MUTEX.get(computer).up();
    }
}