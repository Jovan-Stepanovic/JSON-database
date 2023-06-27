package org.jstepanovic.client.model;


import com.beust.jcommander.Parameter;

public class CommandLineArgs {
    @Parameter(
            names = "-t",
            description = "type of the request (GET, POST, PUT, DELETE)")
    private String type;

    @Parameter(
            names = "-k",
            description = "Key in the database")
    private String key;

    @Parameter(
            names = "-v",
            description = "value to save in the database")
    private String value;

    @Parameter(
            names = "-in",
            description = "Name of the JSON containing request")
    private String fileName;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}

