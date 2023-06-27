package org.jstepanovic.server.model;


import com.google.gson.JsonElement;

public class Response {

    private static final String ERROR = "ERROR";
    private static final String ERROR_MESSAGE = "No such key";
    private static final String OK = "OK";

    private String response;
    private JsonElement value;
    private String reason;

    public static Response ok() {
        return new Response(OK);
    }

    public static Response ok(JsonElement value) {
        return new Response(OK, value);
    }

    public static Response error() {
        return new Response(ERROR, ERROR_MESSAGE);
    }

    public Response() {
    }

    public Response(String response) {
        this.response = response;
    }

    public Response(String response, JsonElement value) {
        this.response = response;
        this.value = value;
    }

    public Response(String response, String reason) {
        this.response = response;
        this.reason = reason;
    }

    public String getResponse() {
        return response;
    }

    public JsonElement getValue() {
        return value;
    }

    public String getReason() {
        return reason;
    }

}
