package org.jstepanovic.client;

import org.jstepanovic.client.service.Client;

public class Main {
    public static void main(String[] args) {
        Client client = new Client();
        client.run(args);
    }
}
