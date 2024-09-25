package com.demo.inventoryapp.inventory.service;

import com.demo.inventoryapp.inventory.repository.InventoryJpaRepositoryStub;
import com.demo.inventoryapp.inventory.service.domain.Inventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    InventoryService sut;
    InventoryJpaRepositoryStub inventoryJpaRepository;

    @BeforeEach
    void setUpAll() {
        inventoryJpaRepository = new InventoryJpaRepositoryStub();
        sut = new InventoryService(inventoryJpaRepository);
    }

    @Nested
    class FindByItemId {
        final String existingItemId = "1";
        final Long stock = 10L;

        @BeforeEach
        void setUp() {
            inventoryJpaRepository.addInventoryEntity(existingItemId, stock);
        }


        @DisplayName("itemId를 갖는 entity를 찾지 못하면 null을 반환한다")
        @Test
        void test1() {
            // given
            final String nonExistingItemId = "2";

            // when
            final Inventory result = sut.findByItemId(nonExistingItemId);

            // then
            assertNull(result);
        }

        @DisplayName("itemId를 갖는 entity를 찾으면 inventory를 반환한다")
        @Test
        void test2() {
            // given
            final String existingItemId = "1";
            final Long stock = 10L;

            // when
            final Inventory result = sut.findByItemId(existingItemId);

            // then
            assertNotNull(result);
            assertEquals(existingItemId, result.getItemId());
            assertEquals(stock, result.getStock());
        }
    }

}