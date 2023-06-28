package client.util;

import org.jstepanovic.client.util.RequestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RequestUtilTest {

    @Test
    public void parseRequestFromCommandLine() {
         final String[] testArgsGet = {"-t", "get", "-k", "1"};
         final String expectedResult = "{\"type\":\"get\",\"key\":\"1\"}";
         Assertions.assertEquals(expectedResult,
                RequestUtil.parseRequest(testArgsGet));

        final String[] testArgsSet = {"-t", "set", "-k", "1", "-v", "HelloWorld!"};
        final String expectedResultSet = "{\"type\":\"set\",\"key\":\"1\",\"value\":\"HelloWorld!\"}";
        Assertions.assertEquals(expectedResultSet,
                RequestUtil.parseRequest(testArgsSet));

        final String[] testArgsDelete = {"-t", "delete", "-k", "name"};
        final String expectedResultDelete = "{\"type\":\"delete\",\"key\":\"name\"}";
        Assertions.assertEquals(expectedResultDelete,
                RequestUtil.parseRequest(testArgsDelete));
    }

    @Test
    public void parseRequestFromJsonFile() {
        final String[] testArgs = {"-in", "getFile.json"};
        final String expectedResult = "{\"type\":\"get\",\"key\":[\"person\",\"name\"]}";

        Assertions.assertEquals(expectedResult,
                RequestUtil.parseRequest(testArgs));

        final String[] testArgsSet = {"-in", "setFile.json"};
        final String expectedResultSet = "{\"type\":\"set\",\"key\":\"person\",\"value\":{\"name\":\"Elon Musk\",\"car\":{\"model\":\"Tesla Roadster\",\"year\":\"2018\"},\"rocket\":{\"name\":\"Falcon 9\",\"launches\":\"87\"}}}";
        Assertions.assertEquals(expectedResultSet,
                RequestUtil.parseRequest(testArgsSet));
    }
}
