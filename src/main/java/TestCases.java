import commons.TheTragedy;
import org.junit.Test;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static commons.TheTragedy.dumpLine;
import static commons.TheTragedy.log;
import static commons.TheTragedy.snooze;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public abstract class TestCases {

    static Strategy Lockless = Runnable::run;

    static final Object implicitLock = new Object();
    static Strategy SynchronizedBlock = (Runnable task) -> {
        synchronized (implicitLock) {
            task.run();
        }
    };

    static final Lock realLock = new ReentrantLock();
    static Strategy UninterruptibleLock = (Runnable task) -> {
        realLock.lock();
        try {
            task.run();
        } finally {
            realLock.unlock();
        }
    };
    static Strategy InterruptibleLock = (Runnable task) -> {
        try {
            realLock.lockInterruptibly();
        } catch (InterruptedException e) {
            log("interrupted");
            return;
        }
        try {
            task.run();
        } finally {
            realLock.unlock();
        }
    };

    @Test
    public void lockLess() throws InterruptedException {
        runWith(Lockless);
    }

    @Test
    public void synchronizedBlock() throws InterruptedException {
        runWith(SynchronizedBlock);
    }

    @Test
    public void uninterruptibleLock() throws InterruptedException {
        runWith(UninterruptibleLock);
    }

    @Test
    public void interruptibleLock() throws InterruptedException {
        runWith(InterruptibleLock);
    }

    private void runWith(Strategy strategy) throws InterruptedException {
        final Semaphore locked = new Semaphore(0);
        final Semaphore done = new Semaphore(0);

        Runnable takeLock = () -> {
            log("trying to lock");
            strategy.runWithLock(() -> {
                log("locked");
                locked.release();
                snooze(Integer.MAX_VALUE);
                log("done");
            });
            done.release();
        };

        startTask("locker", takeLock);

        locked.tryAcquire(1, TimeUnit.SECONDS);

        startTask("loser", takeLock);

        snooze(1000);
        dumpLine("locker");
        dumpLine("loser");

        assertThat("Some locking should be applied", locked.availablePermits(), is(0));

        cancelTask("loser");
        boolean loserCanBeInterrupted = done.tryAcquire(1, TimeUnit.SECONDS);
        log("try " + loserCanBeInterrupted);
        dumpLine("locker");
        dumpLine("loser");

        cancelTask("locker");
        log("try " + done.tryAcquire(1, TimeUnit.SECONDS));

        assertThat("Loser should have aborted when first interrupted", loserCanBeInterrupted, is(true));
    }

    protected abstract void startTask(String name, Runnable task);

    protected abstract void cancelTask(String name);

}
