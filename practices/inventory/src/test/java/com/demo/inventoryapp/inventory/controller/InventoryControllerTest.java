package com.demo.inventoryapp.inventory.controller;

import com.demo.inventoryapp.common.controller.GlobalExceptionHandler;
import com.demo.inventoryapp.config.JsonConfig;
import com.demo.inventoryapp.inventory.controller.consts.ErrorCodes;
import com.demo.inventoryapp.inventory.service.InventoryService;
import com.demo.inventoryapp.inventory.service.domain.Inventory;
import com.demo.inventoryapp.test.fixture.InventoryFixture;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(JsonConfig.class)
@WebMvcTest(controllers = {InventoryController.class, GlobalExceptionHandler.class})
public class InventoryControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    @MockBean
    InventoryService inventoryService;
    @Autowired
    MockMvc mockMvc;

    @DisplayName("재고 조회")
    @Nested
    class GetStock {
        final String itemId = "1";

        @DisplayName("자산이 존재하지 않을 경우, 404 status와 error를 반환한다")
        @Test
        void test1() throws Exception {
            // given
            given(inventoryService.findByItemId(itemId))
                    .willReturn(null);

            // when
            final MvcResult result = mockMvc.perform(get("/api/v1/inventory/{itemId}", itemId))
                    .andExpect(status().isNotFound())
                    .andReturn();

            // then
            final String content = result.getResponse().getContentAsString();
            final JsonNode responseBody = objectMapper.readTree(content);
            final JsonNode errorField = responseBody.get("error");

            assertNotNull(errorField);
            assertTrue(errorField.isObject());
            assertEquals(ErrorCodes.ITEM_NOT_FOUND.code, errorField.get("code").asLong());
            assertEquals(ErrorCodes.ITEM_NOT_FOUND.message, errorField.get("local_message").asText());
            verify(inventoryService).findByItemId(itemId);
        }

        @DisplayName("정상인 경우, 200 status와 결과를 반환한다")
        @Test
        void test2() throws Exception {
            // given
            final Inventory inventory = InventoryFixture.sampleInventory(itemId, null);
            given(inventoryService.findByItemId(itemId))
                    .willReturn(inventory);

            // when
            final MvcResult result = mockMvc.perform(get("/api/v1/inventory/{itemId}", itemId))
                    .andExpect(status().isOk())
                    .andReturn();

            // then
            final String content = result.getResponse().getContentAsString();
            final JsonNode responseBody = objectMapper.readTree(content);
            final JsonNode dataField = responseBody.get("data");

            assertNotNull(dataField);
            assertTrue(dataField.isObject());
            assertEquals(inventory.itemId(), dataField.get("item_id").asText());
            assertEquals(inventory.stock(), dataField.get("stock").asLong());
            verify(inventoryService).findByItemId(itemId);
        }
    }
}
