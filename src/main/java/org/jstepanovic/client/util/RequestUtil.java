package org.jstepanovic.client.util;


import com.beust.jcommander.JCommander;
import com.google.gson.JsonObject;
import org.jstepanovic.client.model.CommandLineArgs;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import static org.jstepanovic.commons.JsonUtil.*;

public class RequestUtil {

    public static final String JSON_REQUEST_REPOSITORY_PATH =
            System.getProperty("user.dir") + "/src/main/java/org/jstepanovic/client/data/%s";


    public static String parseRequest(String[] commandLineArgs) {
        CommandLineArgs request = new CommandLineArgs();
        JCommander.newBuilder()
                .addObject(request)
                .build()
                .parse(commandLineArgs);

        return request.getFileName() == null
                ? GSON.toJson(request)
                : readJsonRequest(request.getFileName());
//                : readJsonRequest(
//                String.format(JSON_REQUEST_REPOSITORY_PATH,
//                        request.getFileName())
//                );

    }

}

