package org.jstepanovic.server.repository;


import com.google.gson.JsonArray;
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
        if (key.isJsonArray()) {
            return setNestedObject(key, value, database);
        }

        database.add(key.getAsString(), value);
        return Response.ok();
    }

    // TODO: 6/29/23 Apply principle from handleGet !!!!!!!!!!!!!!!!!!!!!!!

    private Response setNestedObject(JsonElement key,
                               JsonElement value,
                               JsonObject database) {

        JsonArray jsonArray = key.getAsJsonArray();
        JsonElement jsonNode = database;
        String lastKey = jsonArray.get(jsonArray.size() -1).getAsString();

        for (JsonElement jsonKey: jsonArray) {
            var currentKey = jsonKey.getAsString();

            if(currentKey.equals(lastKey)) {
                jsonNode.getAsJsonObject().add(currentKey, value);
            } else {

                if (!jsonNode.getAsJsonObject().has(currentKey)) {
                    jsonNode.getAsJsonObject().add(currentKey, new JsonObject());
                }
                jsonNode = jsonNode.getAsJsonObject().get(currentKey);
            }
        }
        return Response.ok();
    }


    public String get(JsonElement key) {
        readLock.lock();
        var database = readJSON(JSON_DB_PATH);
        readLock.unlock();

        Response response = handleGet(key, database);
        return GSON.toJson(response);
    }

    private Response handleGet(JsonElement key, JsonObject database) {
        Queue<String> keyQueue = transformToStringQueue(key);
        JsonElement result = getNestedElement(keyQueue, database);
        return result == null
                ? Response.error()
                : Response.ok(result);
    }

    private Queue<String> transformToStringQueue(JsonElement key) {
        Queue<String> nestedKeys = new LinkedList<>();

        if (key.isJsonArray()) {
            for (JsonElement element : key.getAsJsonArray()) {
                nestedKeys.add(element.getAsString());
            }
        } else {
            nestedKeys.add(key.getAsString());
        }

        return nestedKeys;
    }


    //todo Recursion!!!!
//    private Response getNestedObject(JsonArray nestedKeys, JsonObject database) {
//        JsonObject jsonNode = database;
//        String lastKey = nestedKeys.get(nestedKeys.size() -1).getAsString();
//
//        for (JsonElement key: nestedKeys) {
//            var currentKey = key.getAsString();
//
//            if(currentKey.equals(lastKey)) {
//                if (jsonNode.has(currentKey)) {
//                    return Response.ok(jsonNode.get(currentKey));
//                }
//
//                return Response.error();
//            }
//
//            jsonNode = jsonNode.get(currentKey).getAsJsonObject();
//        }
//
//       return Response.error();
//    }


    private JsonElement getNestedElement(Queue<String> keys, JsonObject jsonObject) {
        String currentKey = keys.poll();

        if(keys.peek() == null) {
            if (jsonObject.has(currentKey)) {
                return jsonObject.get(currentKey);
            }
            return null;
        }

       return  getNestedElement(keys, jsonObject.get(currentKey).getAsJsonObject());
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


    // TODO: 6/29/23 Apply principle from handleGet !!!!!!!!!!!!!!!!!!!!!!!
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

