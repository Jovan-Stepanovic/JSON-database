package org.jstepanovic.server.service;

import org.jstepanovic.server.repository.Repository;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.jstepanovic.commons.Constants.SERVER_ADDRESS;
import static org.jstepanovic.commons.Constants.SERVER_PORT;


public enum Server {
    INSTANCE;

    private final Repository repository = new Repository();

    private boolean isOn;


    public void run() {
        try(ServerSocket serverSocket = new ServerSocket(SERVER_PORT, 50, InetAddress.getByName(SERVER_ADDRESS)))
        {
            isOn = true;
            while (isOn) {
                Socket clientSocket = serverSocket.accept();
                Session session = new Session(clientSocket, repository);
                session.run();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop()  {
        isOn = false;
        System.out.println("Server is stopping, won't accept new Clients");
    }

    public boolean isRunning() {
        return isOn;
    }
}
