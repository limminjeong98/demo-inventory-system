package com.demo.inventoryapp.inventory.repository.redis;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@Import(InventoryRedisRepositoryImpl.class)
@Testcontainers
@DataRedisTest
class InventoryRedisRepositoryTest {

    @Container
    private static final GenericContainer<?> redisContainer = new GenericContainer<>("redis:7.2")
            .withExposedPorts(6379);
    // random port (container 밖) <-> 6379 (container 안)

    final String existingItemId = "1";
    final String nonExistingItemId = "2";
    final Long stock = 100L;

    @Autowired
    InventoryRedisRepository sut;

    @Autowired
    StringRedisTemplate redisTemplate;

    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379));
    }

    @BeforeEach
    void setUp() {
        redisTemplate.opsForValue().set("inventory:" + existingItemId, stock.toString());
    }

    @AfterEach
    void tearDown() {
        // nonExistingItemId로 생성되는 데이터는 삭제 필요
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    @Nested
    class GetStock {

        @DisplayName("itemId를 갖는 inventory가 없다면, null을 반환한다")
        @Test
        void test1() {
            // when
            final Long result = sut.getStock(nonExistingItemId);

            // then
            assertNull(result);
        }

        @DisplayName("itemId를 갖는 inventory가 있다면, stock을 반환한다")
        @Test
        void test2() {
            // when
            final Long result = sut.getStock(existingItemId);

            // then
            assertNotNull(result);
            assertEquals(stock, result);
        }
    }

    @Nested
    class DecreaseStock {

        final Long quantity = 10L;

        @DisplayName("itemId를 갖는 inventory가 없다면, inventory를 생성하고 stock을 차감하고 반환한다")
        @Test
        void test1() {
            // when
            final Long result = sut.decreaseStock(nonExistingItemId, quantity);

            // when
            assertEquals(-quantity, result);

        }

        @DisplayName("itemId를 갖는 inventory가 있다면, stock을 차감하고 반환한다")
        @Test
        void test2() {
            // when
            final Long result = sut.decreaseStock(existingItemId, quantity);

            // then
            assertNotNull(result);
            assertEquals(stock - quantity, result);
        }
    }

    @Nested
    class DeleteStock {
        @DisplayName("itemId를 갖는 inventory가 없다면, false를 반환한다")
        @Test
        void test1() {
            // when
            final Boolean result = sut.deleteStock(nonExistingItemId);

            // then
            assertFalse(result);
        }

        @DisplayName("itemId를 갖는 inventory가 있다면, stock을 삭제하고 true를 반환한다")
        @Test
        void test2() {
            // when
            final Boolean result = sut.deleteStock(existingItemId);

            // then
            assertTrue(result);

            final Long totalSize = redisTemplate.getConnectionFactory().getConnection().serverCommands().dbSize();
            assertEquals(0, totalSize);
        }
    }

    @Nested
    class SetStock {

        final Long newStock = 200L;

        @DisplayName("itemId를 갖는 inventory가 없다면, inventory를 생성하고 stock을 수정하고 반환한다")
        @Test
        void test1() {
            // when
            final Long result = sut.setStock(nonExistingItemId, newStock);

            // then
            assertEquals(newStock, result);
        }

        @DisplayName("itemId를 갖는 inventory가 있다면, stock을 수정하고 반환한다")
        @Test
        void test2() {
            // when
            final Long result = sut.setStock(existingItemId, newStock);

            // then
            assertEquals(newStock, result);
        }
    }


}