package bgu.spl.a2;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Describes a monitor that supports the concept of versioning - its idea is
 * simple, the monitor has a version number which you can receive via the method
 * {@link #getVersion()} once you have a version number, you can call
 * {@link #await(int)} with this version number in order to wait until this
 * version number changes.
 *
 * you can also increment the version number by one using the {@link #inc()}
 * method.
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 */
public class VersionMonitor {
    private AtomicInteger versionM = new AtomicInteger(0);

    public int getVersion() {
        return versionM.get();
    }

    /**
     * Why Syncronized -
     * need to be syncronized Because of a lack of synchronization
     * that can be created by reading from the memo and writing to it,
     * with some treads coming together for the same function
     */
    public synchronized void inc() {
        versionM.set(versionM.get()+1);
        notifyAll();
    }

    public void await(int version) throws InterruptedException {
        while(version == versionM.get()) {
            try {
                this.wait();
            } catch (Exception e) {}
        }
    }
}
