package org.jstepanovic.server.service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.jstepanovic.commons.Constants.SERVER_ADDRESS;
import static org.jstepanovic.commons.Constants.SERVER_PORT;


public class Server {

    private final int numberOfProcessors;

    private static Server instance;

    private Server() {
        numberOfProcessors = Runtime.getRuntime().availableProcessors();
    }

    public static Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }

    public void run() {
        try(ServerSocket server = new ServerSocket(SERVER_PORT, 50, InetAddress.getByName(SERVER_ADDRESS))) {
            System.out.println("Server started");

            ExecutorService executorService = Executors.newFixedThreadPool(numberOfProcessors);

            Future<Boolean> future;
            do {
                Socket clientSocket = server.accept();
                future = executorService.submit(new Session(clientSocket));
            } while (future.get());

            executorService.shutdown();

        } catch (IOException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
