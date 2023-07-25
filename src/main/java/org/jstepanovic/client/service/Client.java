package org.jstepanovic.client.service;

import org.jstepanovic.client.util.RequestUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import static org.jstepanovic.commons.Constants.SERVER_ADDRESS;
import static org.jstepanovic.commons.Constants.SERVER_PORT;

public class Client {
    public void run(String[] args) {
        try(
            Socket socket = new Socket(InetAddress.getByName(SERVER_ADDRESS), SERVER_PORT);
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream())
        ) {
            System.out.println("Client started!");

            String jsonRequest = RequestUtil.parseRequest(args); // parse command line arguments

            output.writeUTF(jsonRequest); // sending request to the server
            System.out.printf("Sent: %s\n", jsonRequest);

            String jsonResponse = input.readUTF(); // response message
            System.out.printf("Received: %s\n", jsonResponse);

        } catch (IOException e) {
            System.out.println("Server does not respond, couldn't create communication chanel with the server");
        }
    }
}
