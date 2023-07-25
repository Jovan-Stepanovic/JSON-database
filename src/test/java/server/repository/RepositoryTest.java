package server.repository;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.jstepanovic.commons.JsonUtil;
import org.jstepanovic.server.repository.Repository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import util.TestUtil;

import static org.jstepanovic.commons.JsonUtil.GSON;

public class RepositoryTest {

    private static Repository repository;
    private static String okResponse;
    private static String errorResponse;

    private static final String JSON_DB_PATH = String.format(
            "%s/src/main/java/org/jstepanovic/server/data/db.json",
            System.getProperty("user.dir")
    );

    @BeforeAll
    public static void init() {
        repository = new Repository();
        okResponse = "{\"response\":\"OK\"}";
        errorResponse = "{\"response\":\"ERROR\",\"reason\":\"No such key\"}";
    }

    // Set and Update
    @Test
    public void createNewEntryInDatabaseTest() {
        clearDatabase();

        String response = repository.set(new JsonPrimitive("key"),
                new JsonPrimitive("value"));
        String expectedResponse = "{\"response\":\"OK\"}";

        Assertions.assertEquals(expectedResponse, response);
        Assertions.assertEquals(
                GSON.fromJson("{\"key\":\"value\"}", JsonObject.class),
                JsonUtil.readJSON(JSON_DB_PATH));
    }

    @Test
    public void createNewNestedStructureTest() {
        clearDatabase();

        JsonObject nestedObject = new JsonObject();
        nestedObject.add("k1", new JsonPrimitive("v1"));
        nestedObject.add("k2", new JsonPrimitive("v2"));

        JsonArray complexKey = new JsonArray(2);
        complexKey.add("newKey");
        complexKey.add("nestedObject");

        String response = repository.set(complexKey, nestedObject);
        Assertions.assertEquals(okResponse, response);

        String expectedDatabase = "{\"newKey\":{\"nestedObject\":{\"k1\":\"v1\",\"k2\":\"v2\"}}}";
        Assertions.assertEquals(
                GSON.fromJson(expectedDatabase, JsonObject.class),
                JsonUtil.readJSON(JSON_DB_PATH));
    }

    @Test
    public void updateNestedStructureTest() {
        clearDatabase();

        String databaseBeforeOperation = "{\"newKey\":{\"nestedObject\":{\"k1\":\"v1\",\"k2\":\"v2\"}}}";
        JsonUtil.writeJSON(JSON_DB_PATH, GSON.fromJson(databaseBeforeOperation, JsonObject.class));

        JsonArray complexKey = new JsonArray(3);
        complexKey.add("newKey");
        complexKey.add("nestedObject");
        complexKey.add("k2");

        JsonObject nestedObject = new JsonObject();
        nestedObject.add("testKey", new JsonPrimitive("testValue"));
        String response = repository.set(complexKey, nestedObject);
        Assertions.assertEquals(okResponse, response);

        String expectedDatabase = "{\"newKey\":{\"nestedObject\":{\"k1\":\"v1\",\"k2\":{\"testKey\":\"testValue\"}}}}";

        Assertions.assertEquals(
                GSON.fromJson(expectedDatabase, JsonObject.class),
                JsonUtil.readJSON(JSON_DB_PATH));
    }

    // Get
    @Test
    public void getEntryFromDatabaseTest() {
        clearDatabase();

        String databaseBeforeOperation = "{\"person\":\"Elon Musk\"}";
        JsonUtil.writeJSON(JSON_DB_PATH, GSON.fromJson(databaseBeforeOperation, JsonObject.class));

        String expectedResponse = "{\"response\":\"OK\",\"value\":\"Elon Musk\"}";

        Assertions.assertEquals(expectedResponse,
                repository.get(new JsonPrimitive("person")));
    }

    @Test
    public void getNestedObjectFromDatabase() {
        clearDatabase();

        String databaseBeforeOperation = "{\"person\":{\"name\":\"Elon Musk\"," +
                "\"car\":{\"model\":\"Tesla Roadster\"}," +
                "\"rocket\":{\"name\":\"Falcon 9\",\"launches\":\"88\"}}}";
        JsonUtil.writeJSON(JSON_DB_PATH, GSON.fromJson(databaseBeforeOperation, JsonObject.class));

        JsonArray complexKey = new JsonArray(3);
        complexKey.add("person");
        complexKey.add("rocket");
        complexKey.add("launches");

        String expectedResponse = "{\"response\":\"OK\",\"value\":\"88\"}";

        Assertions.assertEquals(expectedResponse, repository.get(complexKey));
    }

    @Test
    public void objectNotFoundTest() {
        clearDatabase();

        String databaseBeforeOperation = "{\"person\":{\"name\":\"Elon Musk\"," +
                "\"car\":{\"model\":\"Tesla Roadster\"}," +
                "\"rocket\":{\"name\":\"Falcon 9\",\"launches\":\"88\"}}}";
        JsonUtil.writeJSON(JSON_DB_PATH, GSON.fromJson(databaseBeforeOperation, JsonObject.class));

        JsonArray complexKey = new JsonArray(3);
        complexKey.add("person");
        complexKey.add("rocket");
        complexKey.add("wrongKey");

        Assertions.assertEquals(errorResponse, repository.get(complexKey));
    }

    // Delete

    @Test
    public void deleteNestedObjectFromDatabase() {
        clearDatabase();

        String databaseBeforeOperation = "{\"person\":{\"name\":\"Elon Musk\"," +
                "\"car\":{\"model\":\"Tesla Roadster\"}," +
                "\"rocket\":{\"name\":\"Falcon 9\",\"launches\":\"88\"}}}";
        JsonUtil.writeJSON(JSON_DB_PATH, GSON.fromJson(databaseBeforeOperation, JsonObject.class));

        JsonArray complexKey = new JsonArray(3);
        complexKey.add("person");
        complexKey.add("rocket");
        complexKey.add("launches");

        String expectedDatabase = "{\"person\":{\"name\":\"Elon Musk\"," +
                "\"car\":{\"model\":\"Tesla Roadster\"}," +
                "\"rocket\":{\"name\":\"Falcon 9\"}}}";

        Assertions.assertEquals(okResponse, repository.delete(complexKey));
        Assertions.assertEquals(
                GSON.fromJson(expectedDatabase, JsonObject.class),
                JsonUtil.readJSON(JSON_DB_PATH));

    }

    @Test
    public void deleteWithWrongKeyNestedObjectFromDatabase() {
        clearDatabase();

        String database = "{\"person\":{\"name\":\"Elon Musk\"," +
                "\"car\":{\"model\":\"Tesla Roadster\"}," +
                "\"rocket\":{\"name\":\"Falcon 9\",\"launches\":\"88\"}}}";
        JsonUtil.writeJSON(JSON_DB_PATH, GSON.fromJson(database, JsonObject.class));

        JsonArray complexKey = new JsonArray(3);
        complexKey.add("person");
        complexKey.add("grrr");
        complexKey.add("wrongKey");

        Assertions.assertEquals(errorResponse, repository.delete(complexKey));
        Assertions.assertEquals(
                GSON.fromJson(database, JsonObject.class),
                JsonUtil.readJSON(JSON_DB_PATH));

    }


    @AfterAll
    public static void clearDatabase() {
        TestUtil.clearDatabase();
    }


}
