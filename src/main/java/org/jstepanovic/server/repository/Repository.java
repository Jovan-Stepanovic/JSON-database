package org.jstepanovic.server.repository;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jstepanovic.server.model.Response;


import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static org.jstepanovic.commons.JsonUtil.*;



public class Repository {

    public static final String JSON_DB_PATH = String.format(
        "%s/src/main/java/org/jstepanovic/server/data/db.json",
        System.getProperty("user.dir")
    );


    private final Lock readLock;
    private final Lock writeLock;

    public Repository() {
        ReadWriteLock lock = new ReentrantReadWriteLock();
        readLock = lock.readLock();
        writeLock = lock.writeLock();
    }


    public String set(JsonElement key, JsonElement value) {
        readLock.lock();
        var database = readJSON(JSON_DB_PATH);
        readLock.unlock();

        Response response = handleSet(key, value, database);

        writeLock.lock();
        writeJSON(JSON_DB_PATH, database);
        writeLock.unlock();

        return GSON.toJson(response);
    }


    private Response handleSet(JsonElement key,
                                     JsonElement value,
                                     JsonObject database) {
        if (key.isJsonArray()) {
            return setNestedObject(key, value, database);
        }

        database.add(key.getAsString(), value);
        return Response.ok();
    }

    private Response setNestedObject(JsonElement key,
                               JsonElement value,
                               JsonObject database) {

        JsonArray jsonArray = key.getAsJsonArray();
        JsonElement jsonNode = database;

        for (JsonElement jsonKey: jsonArray) {
            var currentKey = jsonKey.getAsString();

            // todo will only work for setting the last child
            try {
                jsonNode = jsonNode.getAsJsonObject().get(currentKey).getAsJsonObject();
            } catch (Exception e) {
                jsonNode.getAsJsonObject().add(jsonKey.getAsString(), value);
                return Response.ok();
            }
        }
        return Response.error();
    }


    public String get(JsonElement key) {
        readLock.lock();
        var database = readJSON(JSON_DB_PATH);
        readLock.unlock();

        Response response = handleGet(key, database);
        return GSON.toJson(response);
    }

    private Response handleGet(JsonElement key, JsonObject database) {
        if (key.isJsonArray()) {
            return getNestedObject(key, database);
        }

        return database.has(key.getAsString())
                ? Response.ok(database.get(key.getAsString()))
                : Response.error();
    }


    private Response getNestedObject(JsonElement key, JsonObject database) {
        JsonArray nestedKeys = key.getAsJsonArray();
        JsonElement jsonNode = database;

        for (JsonElement jsonKey: nestedKeys) {
            var currentKey = jsonKey.getAsString();

            try {
                jsonNode = jsonNode.getAsJsonObject().get(currentKey);
            } catch (Exception e) {
                return Response.error();
            }
        }

       return Response.ok(jsonNode);
    }


    public String delete(JsonElement key) {
        readLock.lock();
        var database = readJSON(JSON_DB_PATH);
        readLock.unlock();

        Response response = handleDelete(key, database);

        writeLock.lock();
        writeJSON(JSON_DB_PATH, database);
        writeLock.unlock();

        return GSON.toJson(response);
    }

    private Response handleDelete(JsonElement key, JsonObject database) {

        if (key.isJsonArray()) {
            return deleteNestedObject(key.getAsJsonArray(), database);
        }

        if (!database.has(key.getAsString())) {
            return Response.error();
        }

        database.remove(key.getAsString());
        return Response.ok();
    }


    private Response deleteNestedObject(JsonArray nestedKeys, JsonObject database) {
        JsonElement jsonNode = database;

        for (JsonElement key: nestedKeys) {
            String currentKey = key.getAsString();
            try {
                jsonNode = jsonNode
                        .getAsJsonObject()
                        .get(currentKey)
                        .getAsJsonObject();

            } catch (Exception e) {
                //IllegalStateException not JSONObject -> part of functionality
                if (e instanceof IllegalStateException) {
                    jsonNode.getAsJsonObject().remove(currentKey);
                    return Response.ok();
                }

                // NPE -> bad request return ErrorResponse
                if (e instanceof NullPointerException) {
                    return Response.error();
                }
            }
        }

        return Response.error();
    }


}

