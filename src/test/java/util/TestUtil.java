package util;

import com.google.gson.JsonObject;
import org.jstepanovic.commons.JsonUtil;

import static org.jstepanovic.server.repository.Repository.JSON_DB_PATH;

public class TestUtil {

    public static void clearDatabase(){
        JsonUtil.writeJSON(JSON_DB_PATH, new JsonObject());
    }
}
