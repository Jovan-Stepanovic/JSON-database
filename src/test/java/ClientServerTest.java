import org.jstepanovic.client.service.Client;
import org.jstepanovic.server.service.Server;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ClientServerTest {

    @Test
    void clientServerConnectionTest() throws InterruptedException {
        final String[] setArgs = {"-in", "setFile.json"};
        final String[] getArgs = {"-in", "getFile.json"};
        final String[] deleteArgs = {"-in", "deleteFile"};
        final String[] exitArgs = {"-t", "exit"};

        Server server = Server.getInstance();
        Client client = new Client();
        Client client2 = new Client();
        Client client3 = new Client();
        Client client4 = new Client();

        ExecutorService executor = Executors.newFixedThreadPool(4);

        executor.submit(server::run);
        TimeUnit.MILLISECONDS.sleep(200);

        executor.submit(() -> client.run(setArgs));
        TimeUnit.MILLISECONDS.sleep(50);
        executor.submit(() -> client2.run(getArgs));
        TimeUnit.MILLISECONDS.sleep(50);
        executor.submit(() -> client3.run(deleteArgs));

        TimeUnit.MILLISECONDS.sleep(300);
        executor.submit(() -> client4.run(exitArgs));

        executor.shutdown();
        executor.awaitTermination(200, TimeUnit.MILLISECONDS);


        Assertions.assertTrue(executor.isShutdown());
        Assertions.assertTrue(executor.isTerminated());

    }
}
