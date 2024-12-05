package com.demo.inventoryapp.inventory.repository;

import com.demo.inventoryapp.inventory.repository.jpa.InventoryJpaRepositoryStub;
import com.demo.inventoryapp.inventory.service.domain.Inventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class InventoryPersistenceAdapterImplTest {

    @InjectMocks
    InventoryPersistenceAdapterImpl sut;

    @Spy
    InventoryJpaRepositoryStub inventoryJpaRepositoryStub;

    @Nested
    class FindByItemId {
        final String existingItemId = "1";
        final String nonExistingItemId = "2";
        final Long stock = 100L;

        @BeforeEach
        void setUp() {
            inventoryJpaRepositoryStub.addInventoryEntity(existingItemId, stock);
        }

        @DisplayName("itemId를 갖는 entity가 없다면, null을 반환한다")
        @Test
        void test1() {
            // when
            final Inventory result = sut.findByItemId(nonExistingItemId);

            // then
            assertNull(result);
        }

        @DisplayName("itemId를 갖는 entity가 있다면, inventory를 반환한다")
        @Test
        void test2() {
            // when
            Inventory result = sut.findByItemId(existingItemId);

            // then
            assertNotNull(result);
            assertEquals(existingItemId, result.getItemId());
            assertEquals(stock, result.getStock());
        }
    }

    @Nested
    class DecreaseByItemId {
        final String existingItemId = "1";
        final String nonExistingItemId = "2";
        final Long stock = 100L;

        @BeforeEach
        void setUp() {
            inventoryJpaRepositoryStub.addInventoryEntity(existingItemId, stock);
        }

        @DisplayName("itemId를 갖는 entity가 없다면, null을 반환한다")
        @Test
        void test1() {
            // When
            Inventory result = sut.decreaseStock(nonExistingItemId, 10L);

            // Then
            assertNull(result);
        }

        @DisplayName("itemId를 갖는 entity가 있다면, stock을 차감하고 inventory를 반환한다")
        @Test
        void test2() {
            // When
            Inventory result = sut.decreaseStock(existingItemId, 10L);

            // Then
            assertNotNull(result);

            final Long expectedStock = stock - 10L;
            assertEquals(expectedStock, result.getStock());
        }
    }

    @Nested
    class Save {
        final Long existingId = 1L;
        final String existingItemId = "1";
        final String nonExistingItemId = "2";
        final Long oldStock = 10L;

        @BeforeEach
        void setUp() {
            inventoryJpaRepositoryStub.addInventoryEntity(existingItemId, oldStock);
        }

        @DisplayName("id를 갖는 entity가 없다면, entity를 추가하고 추가된 inventory를 반환한다")
        @Test
        void test1() {
            // Given
            final Long newStock = 1234L;

            // When
            final Inventory inventory = new Inventory(null, nonExistingItemId, newStock);
            final Inventory result = sut.save(inventory);

            // Then
            assertNotNull(result);
            assertEquals(nonExistingItemId, result.getItemId());
            assertEquals(newStock, result.getStock());
        }

        @DisplayName("id를 갖는 entity가 있다면, entity를 수정하고 수정된 inventory를 반환한다")
        @Test
        void test2() {
            // Given
            final Long newStock = 1234L;
            final Inventory inventory = sut.findByItemId(existingItemId);

            // When
            inventory.setStock(newStock);
            final Inventory result = sut.save(inventory);

            // Then
            assertEquals(1L, result.getId());
            assertEquals(existingItemId, result.getItemId());
            assertEquals(newStock, result.getStock());
        }
    }

}