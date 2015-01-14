@FunctionalInterface
public interface Strategy {
    public void runWithLock(Runnable task);
}
