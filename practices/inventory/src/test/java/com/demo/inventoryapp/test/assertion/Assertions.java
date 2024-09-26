package com.demo.inventoryapp.test.assertion;

import com.demo.inventoryapp.inventory.controller.consts.ErrorCodes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

public class Assertions {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

    public static void assertMvcErrorEquals(MvcResult result, ErrorCodes errorCodes) throws UnsupportedEncodingException, JsonProcessingException {
        String content = result.getResponse().getContentAsString();
        final JsonNode responseBody = objectMapper.readTree(content);
        final JsonNode errorField = responseBody.get("error");

        assertNotNull(errorField);
        assertTrue(errorField.isObject());
        assertEquals(errorCodes.code, errorField.get("code").asLong());
        assertEquals(errorCodes.message, errorField.get("local_message").asText());
    }

    public static void assertMvcDataEquals(MvcResult result, Consumer<JsonNode> consumer) throws UnsupportedEncodingException, JsonProcessingException {
        final String content = result.getResponse().getContentAsString();
        final JsonNode responseBody = objectMapper.readTree(content);
        final JsonNode dataField = responseBody.get("data");

        assertNotNull(dataField);
        assertTrue(dataField.isObject());

        consumer.accept(dataField);
    }
}