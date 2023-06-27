package org.jstepanovic.server.model.command;


import com.google.gson.JsonObject;

public interface Command {

    String GET = "get";
    String SET = "set";
    String DELETE = "delete";

    String execute(JsonObject request);
}
