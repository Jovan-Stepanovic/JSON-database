package org.jstepanovic.commons;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JsonUtil {

    public static final Gson GSON = new GsonBuilder()
//            .setPrettyPrinting()
            .create();

    public static void writeJSON(String pathToJsonFile, JsonObject data) {

        Path path = Paths.get(pathToJsonFile);
        if (Files.notExists(path)) {
            throw new RuntimeException("Invalid path to JSON database");
        }

        try (FileWriter writer = new FileWriter(pathToJsonFile)) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't write to JSON database", e);
        }
    }

    public static String readJsonRequest(String requestFileName) {
        InputStream inputStream = JsonUtil.class.getResourceAsStream("/requests/" + requestFileName);

        if (inputStream == null) {
            throw new RuntimeException("JSON request file with name you provided doesn't exist, fileName: " + requestFileName);
        }

        Reader reader = new InputStreamReader(inputStream);
        JsonObject request =  GSON.fromJson(reader, JsonObject.class);
        return GSON.toJson(request);
    }

    public static JsonObject readJSON(String pathToJsonFile) {
        try (FileReader reader = new FileReader(pathToJsonFile)) {
            JsonObject database = GSON.fromJson(reader, JsonObject.class);
            return database == null ? new JsonObject() : database;
        } catch (IOException e) {
            throw new RuntimeException("Invalid path to JSON database", e);
        }
    }
}
