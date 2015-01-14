import org.junit.After;
import org.junit.Before;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ExecutorGames extends TestCases {

    private ExecutorService executor;
    private Map<String, Future<?>> futures = new HashMap<>();

    @Before
    public void setUp() throws Exception {
        executor = Executors.newFixedThreadPool(2);
    }

    @After
    public void tearDown() throws Exception {
        executor.shutdownNow();
    }

    @Override
    protected void startTask(String name, Runnable task) {
        futures.put(name, executor.submit(nameAndRun(name, task)));
    }

    @Override
    protected void cancelTask(String name) {
        boolean cancel = futures.get(name).cancel(true);
        log("future reckons " + name + " is cancelled " + cancel);
    }

    private Runnable nameAndRun(String name, Runnable task) {
        return () -> {
            Thread.currentThread().setName(name);
            task.run();
        };
    }
}
