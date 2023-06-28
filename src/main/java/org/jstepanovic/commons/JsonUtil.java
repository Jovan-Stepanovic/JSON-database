package org.jstepanovic.commons;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.*;

public class JsonUtil {

    public static final Gson GSON = new GsonBuilder()
//            .setPrettyPrinting()
            .create();

    public static void writeJSON(String pathToJsonFile, JsonObject data) {
        try (FileWriter writer = new FileWriter(pathToJsonFile)) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public static String readJsonRequest(String pathToJsonFile) {
//        JsonObject request = readJSON(pathToJsonFile);
//        return GSON.toJson(request);
//    }

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
            return GSON.fromJson(reader, JsonObject.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JsonObject();
    }
}
