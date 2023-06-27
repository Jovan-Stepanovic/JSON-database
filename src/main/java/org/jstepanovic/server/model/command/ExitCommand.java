package org.jstepanovic.server.model.command;

import com.google.gson.JsonObject;
import org.jstepanovic.server.model.Response;

import static org.jstepanovic.commons.JsonUtil.GSON;


public class ExitCommand implements Command {

    @Override
    public String execute(JsonObject request) {
        Response response = Response.ok();
        return GSON.toJson(response);
    }
}
