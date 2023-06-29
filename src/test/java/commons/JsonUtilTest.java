package commons;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.jstepanovic.commons.JsonUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JsonUtilTest {
    private static final String JSON_DB_PATH = String.format(
            "%s/src/main/java/org/jstepanovic/server/data/db.json",
            System.getProperty("user.dir")
    );

    private static final String  WRONG_PATH_TO_FILE = "wrongPathToDatabaseOrFilename";


    @Test
    public void readWriteDatabaseTest() {
        JsonObject database = JsonUtil.readJSON(JSON_DB_PATH);
        Assertions.assertEquals(new JsonObject(), database);

        JsonObject newDatabase = new JsonObject();
        newDatabase.add("bla", new JsonPrimitive("truc"));
        JsonUtil.writeJSON(JSON_DB_PATH, newDatabase);

        Assertions.assertEquals(newDatabase,
                JsonUtil.readJSON(JSON_DB_PATH));
    }


    @Test
    public void readDatabaseWrongFilePath() {
        Assertions.assertThrows(RuntimeException.class,
                () -> JsonUtil.readJSON(WRONG_PATH_TO_FILE));
    }

    @Test
    public void writeDatabaseWrongFilePath() {
        Assertions.assertThrows(RuntimeException.class,
                () -> JsonUtil.writeJSON(WRONG_PATH_TO_FILE, new JsonObject()));
    }

    @Test
    public void readJsonRequestWrongFileName() {
        Assertions.assertThrows(RuntimeException.class,
                () -> JsonUtil.readJsonRequest(WRONG_PATH_TO_FILE));
    }


    @BeforeEach
    public void clearDatabase() {
        clear();
    }

    @AfterAll
    public static void clear(){
        JsonUtil.writeJSON(JSON_DB_PATH, new JsonObject());
    }
}
