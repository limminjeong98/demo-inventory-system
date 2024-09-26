package com.demo.inventoryapp.integration;

import com.demo.inventoryapp.inventory.controller.consts.ErrorCodes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("integration-test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {"classpath:schema.sql", "classpath:data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class InventoryIntegrationTest {

    final String existingItemId = "1";
    final String nonExistingItemId = "2";
    final Long stock = 100L;

    @Autowired
    private MockMvc mockMvc;

    @DisplayName("재고 조회 실패")
    @Test
    void test1() throws Exception {
        mockMvc.perform(get("/api/v1/inventory/{itemId}", nonExistingItemId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value(ErrorCodes.ITEM_NOT_FOUND.code))
                .andExpect(jsonPath("$.error.local_message").value(ErrorCodes.ITEM_NOT_FOUND.message));
    }

    @DisplayName("재고 조회 성공")
    @Test
    void test2() throws Exception {
        mockMvc.perform(get("/api/v1/inventory/{itemId}", existingItemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.item_id").value(existingItemId))
                .andExpect(jsonPath("$.data.stock").value(stock));
    }

    @DisplayName("재고 차감 실패")
    @Test
    void test3() throws Exception {
        // 1. 재고를 조회하고 100개인 것을 확인한다
        successGetStock(existingItemId, stock);

        // 2. 재고 110개를 차감하고 실패한다
        final Long quantity = 110L;
        final String requestBody = "{\"quantity\": " + quantity + "}";
        mockMvc.perform(
                        post("/api/v1/inventory/{itemId}/decrease", existingItemId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value(ErrorCodes.INSUFFICIENT_STOCK.code))
                .andExpect(jsonPath("$.error.local_message").value(ErrorCodes.INSUFFICIENT_STOCK.message));

        // 3. 재고를 조회하고 100개인 것을 확인한다
        successGetStock(existingItemId, stock);
    }

    @DisplayName("재고 차감 성공")
    @Test
    void test4() throws Exception {
        // 1. 재고를 조회하고 100개인 것을 확인한다
        successGetStock(existingItemId, stock);

        // 2. 재고 10개를 차감하고 성공한다
        final Long quantity = 10L;
        final Long expectedStock = stock - quantity;
        final String requestBody = "{\"quantity\": " + quantity + "}";
        mockMvc.perform(
                        post("/api/v1/inventory/{itemId}/decrease", existingItemId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.item_id").value(existingItemId))
                .andExpect(jsonPath("$.data.stock").value(expectedStock));

        // 3. 재고를 조회하고 90개인 것을 확인한다
        successGetStock(existingItemId, expectedStock);
    }

    @DisplayName("재고 수정 실패")
    @Test
    void test5() throws Exception {
        // 1. 재고를 조회하고 100개인 것을 확인한다
        successGetStock(existingItemId, stock);

        // 2. 재고를 -100개로 수정하고 실패한다
        final Long newStock = -100L;
        final String requestBody = "{\"stock\": " + newStock + "}";
        mockMvc.perform(
                        patch("/api/v1/inventory/{itemId}/stock", existingItemId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value(ErrorCodes.INVALID_STOCK.code))
                .andExpect(jsonPath("$.error.local_message").value(ErrorCodes.INVALID_STOCK.message));

        // 3. 재고를 조회하고 100개인 것을 확인한다
        successGetStock(existingItemId, stock);
    }

    @DisplayName("재고 수정 성공")
    @Test
    void test6() throws Exception {
        // 1. 재고를 조회하고 100개인 것을 확인한다
        successGetStock(existingItemId, stock);

        // 2. 재고를 1000개로 수정하고 실패한다
        final Long newStock = 1000L;
        final String requestBody = "{\"stock\": " + newStock + "}";
        mockMvc.perform(
                        patch("/api/v1/inventory/{itemId}/stock", existingItemId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.item_id").value(existingItemId))
                .andExpect(jsonPath("$.data.stock").value(newStock));

        // 3. 재고를 조회하고 1000개인 것을 확인한다
        successGetStock(existingItemId, newStock);
    }

    @DisplayName("재고 차감, 수정 종합")
    @Test
    void test7() throws Exception {

        Long expectedStock = 100L;

        // 1. 재고를 조회하고 100개인 것을 확인한다
        successGetStock(existingItemId, expectedStock);

        // 2. 재고를 10개 차감을 7번 반복하고 성공한다
        final Long decreaseQuantity = 10L;
        for (int i = 0; i < 7; i++) {
            expectedStock -= decreaseQuantity;
            final String requestBody = "{\"quantity\": " + decreaseQuantity + "}";
            mockMvc.perform(
                            post("/api/v1/inventory/{itemId}/decrease", existingItemId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(requestBody)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.item_id").value(existingItemId))
                    .andExpect(jsonPath("$.data.stock").value(expectedStock));
        }

        // 3. 재고를 조회하고 30개인 것을 확인한다.
        successGetStock(existingItemId, 30L);

        // 4. 재고를 500개로 수정하고 성공한다.
        final Long newStock = 500L;
        final String requestBody = "{\"stock\": " + newStock + "}";
        mockMvc.perform(
                        patch("/api/v1/inventory/{itemId}/stock", existingItemId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.item_id").value(existingItemId))
                .andExpect(jsonPath("$.data.stock").value(newStock));

        // 5. 재고를 조회하고 500개인 것을 확인한다.
        successGetStock(existingItemId, 500L);
    }

    private void successGetStock(String itemId, Long stock) throws Exception {
        mockMvc.perform(get("/api/v1/inventory/{itemId}", itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.item_id").value(itemId))
                .andExpect(jsonPath("$.data.stock").value(stock));
    }
}
