package org.jstepanovic.server.model.command;


import com.google.gson.JsonObject;
import org.jstepanovic.server.repository.Repository;


public class DeleteCommand implements Command {

    private Repository repository;

    public DeleteCommand(Repository repository) {
        this.repository = repository;
    }

    @Override
    public String execute(JsonObject request) {
        return repository.delete(request.get("key"));
    }
}
