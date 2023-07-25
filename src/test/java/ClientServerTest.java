import org.jstepanovic.client.service.Client;
import org.jstepanovic.server.service.Server;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import util.TestUtil;

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


        Client client = new Client();
        Client client2 = new Client();
        Client client3 = new Client();
        Client client4 = new Client();

        ExecutorService executor = Executors.newFixedThreadPool(4);

        executor.submit(Server.INSTANCE::run);
        TimeUnit.MILLISECONDS.sleep(500);

        executor.submit(() -> client.run(setArgs));
        executor.submit(() -> client2.run(getArgs));
        executor.submit(() -> client3.run(deleteArgs));
        executor.submit(() -> new Client().run(getArgs));

        TimeUnit.MILLISECONDS.sleep(500);
        executor.submit(() -> client4.run(exitArgs));

        executor.shutdown();


        executor.awaitTermination(300, TimeUnit.MILLISECONDS);

        Assertions.assertTrue(executor.isShutdown());
        Assertions.assertTrue(executor.isTerminated());

        Assertions.assertFalse(Server.INSTANCE.isRunning());

        TestUtil.clearDatabase();
    }



}
