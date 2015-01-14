import commons.TheTragedy;

public class BogStandardThreads extends TestCases {

    @Override
    protected void startTask(String name, Runnable task) {
        Thread t = new Thread(task, name);
        t.setDaemon(true);
        t.start();
    }

    @Override
    protected void cancelTask(String name) {
        TheTragedy.getThread(name).interrupt();
    }

}
