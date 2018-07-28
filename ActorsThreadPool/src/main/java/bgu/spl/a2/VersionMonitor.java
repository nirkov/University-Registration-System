package bgu.spl.a2;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Describes a monitor that supports the concept of versioning - its idea is
 * simple, the monitor has a version number which you can receive via the method
 * {@link #getVersion()} once you have a version number, you can call
 * {@link #await(int)} with this version number in order to wait until this
 * version number changes.
 */
public class VersionMonitor {
    private AtomicInteger versionM = new AtomicInteger(0);

    public int getVersion() {
        return versionM.get();
    }
    
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
