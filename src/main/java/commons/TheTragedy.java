package commons;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Of the commons.
 */
public class TheTragedy {
    public static void log(String m) {
        String now = ZonedDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME);
        System.out.println(now + " [" + Thread.currentThread().getName() + "]: " + m);
    }

    public static void snooze(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            log("woken - how rude");
        }
    }

    public static void dumpLine(String name) {
        Thread info = getThread(name);
        log(name + " is " + (info == null ? " missing" : at(info.getState(), info.getStackTrace())));
    }

    public static String at(Thread.State state, StackTraceElement[] stackTrace) {
        return state + ": " + (stackTrace.length == 0 ? "" : stackTrace[0].toString());
    }

    public static Thread getThread(String name) {
        return Thread.getAllStackTraces().keySet().stream().filter((t) -> t.getName().equals(name)).findFirst().orElse(null);
    }
}
