import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static commons.TheTragedy.dumpLine;
import static commons.TheTragedy.log;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SocketTest {

    @Test
    public void socketReadAintInterruptible() throws IOException, InterruptedException {
        ServerSocket serverSocket = new ServerSocket(8100);

        Thread reader = new Thread(() -> {
            try {
                Socket socket = new Socket("localhost", 8100);
                // can time out but not be interrupted
//                socket.setSoTimeout(500);
                log("connected " + socket.getInetAddress());
                int read = socket.getInputStream().read();
                log("read " + read);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, "reader");
        reader.start();

        Socket client = serverSocket.accept();
        log("client arrived " + client);
        reader.interrupt();
        reader.join(1000);
        dumpLine("reader");
        assertThat(reader.getState(), is(Thread.State.TERMINATED));
    }
}
