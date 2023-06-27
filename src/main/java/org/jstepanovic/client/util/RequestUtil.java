package org.jstepanovic.client.util;


import com.beust.jcommander.JCommander;
import org.jstepanovic.client.model.CommandLineArgs;


import static org.jstepanovic.commons.JsonUtil.*;

public class RequestUtil {
    public static String parseRequest(String[] commandLineArgs) {
        CommandLineArgs request = new CommandLineArgs();
        JCommander.newBuilder()
                .addObject(request)
                .build()
                .parse(commandLineArgs);

        return request.getFileName() == null
                ? GSON.toJson(request)
                : readJsonRequest(request.getFileName());
    }
}

