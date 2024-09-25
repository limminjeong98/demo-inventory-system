package com.demo.inventoryapp.inventory.service;

import com.demo.inventoryapp.inventory.repository.InventoryJpaRepositoryStub;
import com.demo.inventoryapp.inventory.service.domain.Inventory;
import com.demo.inventoryapp.inventory.service.exception.InsufficientStockException;
import com.demo.inventoryapp.inventory.service.exception.InvalidDecreaseQuantityException;
import com.demo.inventoryapp.inventory.service.exception.ItemNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @InjectMocks
    InventoryService sut;

    @Spy
    InventoryJpaRepositoryStub inventoryJpaRepository;

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

    @Nested
    class DecreaseByItemId {

        final String existingItemId = "1";
        final Long stock = 100L;

        @BeforeEach
        void setUp() {
            inventoryJpaRepository.addInventoryEntity(existingItemId, stock);

        }

        @DisplayName("quantity가 음수라면, Exception을 throw한다")
        @Test
        void test1() {
            // given
            final Long quantity = -1L;

            // when & then
            assertThrows(InvalidDecreaseQuantityException.class, () -> {
                sut.decreaseByItemId(existingItemId, quantity);
            });
        }

        @DisplayName("itemId를 갖는 entity를 찾지 못하면, Exception을 throw한다")
        @Test
        void test2() {
            // given
            final Long quantity = 10L;
            final String nonExistingItemId = "2";

            // when & then
            assertThrows(ItemNotFoundException.class, () -> {
                sut.decreaseByItemId(nonExistingItemId, quantity);
            });
        }

        @DisplayName("quantity가 stock보다 크면, Exception을 throw한다")
        @Test
        void test3() {
            // given
            final Long quantity = stock + 1L;

            // when & then
            assertThrows(InsufficientStockException.class, () -> {
                sut.decreaseByItemId(existingItemId, quantity);
            });
        }

        @DisplayName("변경된 entity가 없다면 Exception을 throw한다")
        @Test
        void test4() {
            // given
            final Long quantity = 10L;

            doReturn(0).when(inventoryJpaRepository)
                    .decreaseStock(existingItemId, quantity);

            // when
            assertThrows(ItemNotFoundException.class, () -> {
                sut.decreaseByItemId(existingItemId, quantity);
            });

            // then
            verify(inventoryJpaRepository).decreaseStock(existingItemId, quantity);
        }

        @DisplayName("itemId를 갖는 entity를 찾으면, stock을 차감하고 inventory를 반환한다")
        @Test
        void test5() {
            // given
            final Long quantity = 10L;

            // when
            final Inventory result = sut.decreaseByItemId(existingItemId, quantity);

            // then
            assertNotNull(result);
            assertEquals(existingItemId, result.getItemId());
            assertEquals(stock - quantity, result.getStock());
        }
    }
}