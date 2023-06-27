package org.jstepanovic.server.model.command;

import com.google.gson.JsonObject;
import org.jstepanovic.server.repository.Repository;


public class GetCommand implements Command {
    private final Repository repository;

    public GetCommand(Repository repository) {
        this.repository = repository;
    }

    @Override
    public String execute(JsonObject request) {
        return repository.get(request.get("key"));
    }

}
