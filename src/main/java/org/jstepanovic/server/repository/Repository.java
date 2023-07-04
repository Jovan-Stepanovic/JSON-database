package org.jstepanovic.server.repository;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jstepanovic.server.model.Response;


import java.util.LinkedList;
import java.util.Queue;
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

        Queue<String> complexKey = transformKey(key);
        setNestedObject(complexKey, value, database);
        return Response.ok();
    }


    private JsonObject setNestedObject(Queue<String> keys,
                               JsonElement value,
                               JsonObject jsonNode) {
        if (keys.size() == 1) {
            jsonNode.add(keys.poll(), value);
            return jsonNode;
        }

        if (!jsonNode.has(keys.peek())) {
            jsonNode.add(keys.peek(), new JsonObject());
        }

        jsonNode = jsonNode.get(keys.poll()).getAsJsonObject();
        return setNestedObject(keys, value, jsonNode);
    }

    public String get(JsonElement key) {
        readLock.lock();
        var database = readJSON(JSON_DB_PATH);
        readLock.unlock();

        Response response = handleGet(key, database);
        return GSON.toJson(response);
    }

    private Response handleGet(JsonElement key, JsonObject database) {
        Queue<String> complexKey = transformKey(key);
        JsonObject nestedObject = getNestedObject(complexKey, database);
        return nestedObject == null
                ? Response.error()
                : Response.ok(nestedObject.get(complexKey.poll()));
    }

    private LinkedList<String> transformKey(JsonElement key) {
        LinkedList<String> nestedKeys = new LinkedList<>();
        if (key.isJsonArray()) {
            for (JsonElement element : key.getAsJsonArray()) {
                nestedKeys.add(element.getAsString());
            }
        } else {
            nestedKeys.add(key.getAsString());
        }
        return nestedKeys;
    }

    private JsonObject getNestedObject(Queue<String> keys, JsonObject jsonObject) {
        if (jsonObject.has(keys.peek())) {
            return keys.size() == 1
                    ? jsonObject
                    : getNestedObject(keys, jsonObject.get(keys.poll()).getAsJsonObject());
        }
        return null;
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
        LinkedList<String> complexKey = transformKey(key);
        JsonObject jsonObject = getNestedObject(complexKey, database);

        if (jsonObject == null) {
            return Response.error();
        }

        jsonObject.remove(complexKey.poll());
        return Response.ok();
    }

}

