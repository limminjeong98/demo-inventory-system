package com.demo.inventoryapp.inventory.controller;

import com.demo.inventoryapp.common.controller.GlobalExceptionHandler;
import com.demo.inventoryapp.config.JsonConfig;
import com.demo.inventoryapp.inventory.controller.consts.ErrorCodes;
import com.demo.inventoryapp.inventory.service.InventoryService;
import com.demo.inventoryapp.inventory.service.domain.Inventory;
import com.demo.inventoryapp.inventory.service.exception.InsufficientStockException;
import com.demo.inventoryapp.inventory.service.exception.InvalidDecreaseQuantityException;
import com.demo.inventoryapp.inventory.service.exception.ItemNotFoundException;
import com.demo.inventoryapp.test.fixture.InventoryFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static com.demo.inventoryapp.test.assertion.Assertions.assertMvcDataEquals;
import static com.demo.inventoryapp.test.assertion.Assertions.assertMvcErrorEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(JsonConfig.class)
@WebMvcTest(controllers = {InventoryController.class, GlobalExceptionHandler.class})
public class InventoryControllerTest {

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
            assertMvcErrorEquals(result, ErrorCodes.ITEM_NOT_FOUND);
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
            assertMvcDataEquals(result, dataField -> {
                assertEquals(inventory.itemId(), dataField.get("item_id").asText());
                assertEquals(inventory.stock(), dataField.get("stock").asLong());
            });

            verify(inventoryService).findByItemId(itemId);
        }
    }

    @DisplayName("재고 차감")
    @Nested
    class DecreaseQuantity {
        final String itemId = "1";
        final Long quantity = 10L;

        @DisplayName("자산이 존재하지 않을 경우, 404 status와 error를 반환한다")
        @Test
        void test1() throws Exception {
            // given
            given(inventoryService.decreaseByItemId(itemId, quantity))
                    .willThrow(ItemNotFoundException.class);

            // when
            final String requestBody = "{\"quantity\": " + quantity + "}";
            final MvcResult result = mockMvc.perform(
                            post("/api/v1/inventory/{itemId}/decrease", itemId)
                                    .content(requestBody)
                                    .contentType(MediaType.APPLICATION_JSON)
                    ).andExpect(status().isNotFound())
                    .andReturn();

            // then
            assertMvcErrorEquals(result, ErrorCodes.ITEM_NOT_FOUND);
            verify(inventoryService).decreaseByItemId(itemId, quantity);
        }

        @DisplayName("재고가 부족할 경우, 400 status와 error를 반환한다")
        @Test
        void test2() throws Exception {
            // given
            given(inventoryService.decreaseByItemId(itemId, quantity))
                    .willThrow(InsufficientStockException.class);

            // when
            final String requestBody = "{\"quantity\": " + quantity + "}";
            final MvcResult result = mockMvc.perform(
                            post("/api/v1/inventory/{itemId}/decrease", itemId)
                                    .content(requestBody)
                                    .contentType(MediaType.APPLICATION_JSON)
                    ).andExpect(status().isBadRequest())
                    .andReturn();

            // then
            assertMvcErrorEquals(result, ErrorCodes.INSUFFICIENT_STOCK);
            verify(inventoryService).decreaseByItemId(itemId, quantity);
        }

        @DisplayName("차감 수량이 유효하지 않은 경우, 400 status와 error를 반환한다")
        @Test
        void test3() throws Exception {
            // given
            given(inventoryService.decreaseByItemId(itemId, quantity))
                    .willThrow(InvalidDecreaseQuantityException.class);

            // when
            final String requestBody = "{\"quantity\": " + quantity + "}";
            final MvcResult result = mockMvc.perform(
                            post("/api/v1/inventory/{itemId}/decrease", itemId)
                                    .content(requestBody)
                                    .contentType(MediaType.APPLICATION_JSON)
                    ).andExpect(status().isBadRequest())
                    .andReturn();

            // then
            assertMvcErrorEquals(result, ErrorCodes.INVALID_DECREASE_QUANTITY);
            verify(inventoryService).decreaseByItemId(itemId, quantity);
        }

        @DisplayName("정상인 경우, 200 status와 결과를 반환한다")
        @Test
        void test4() throws Exception {
            // given
            final Inventory inventory = InventoryFixture.sampleInventory(itemId, null);
            given(inventoryService.decreaseByItemId(itemId, quantity))
                    .willReturn(inventory);

            // when
            final String requestBody = "{\"quantity\": " + quantity + "}";
            final MvcResult result = mockMvc.perform(
                            post("/api/v1/inventory/{itemId}/decrease", itemId)
                                    .content(requestBody)
                                    .contentType(MediaType.APPLICATION_JSON)
                    ).andExpect(status().isOk())
                    .andReturn();

            // then
            assertMvcDataEquals(result, dataField -> {
                assertEquals(inventory.itemId(), dataField.get("item_id").asText());
                assertEquals(inventory.stock(), dataField.get("stock").asLong());
            });
            verify(inventoryService).decreaseByItemId(itemId, quantity);
        }
    }
}
