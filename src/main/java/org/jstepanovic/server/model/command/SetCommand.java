package org.jstepanovic.server.model.command;

import com.google.gson.JsonObject;
import org.jstepanovic.server.repository.Repository;

public class SetCommand implements Command {

    private final Repository repository;

    public SetCommand(Repository repository) {
        this.repository = repository;
    }

    @Override
    public String execute(JsonObject request) {
       return repository.set(request.get("key"), request.get("value"));
    }
}
