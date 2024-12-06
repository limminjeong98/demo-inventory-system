package com.demo.inventoryapp.inventory.repository.redis;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class InventoryRedisRepositoryImpl implements InventoryRedisRepository {

    private final StringRedisTemplate redisTemplate;
    // private final ValueOperations<String, String> valueOperations;

    @Autowired
    public InventoryRedisRepositoryImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        // this.valueOperations = redisTemplate.opsForValue();
    }

    @Override
    public @NotNull String key(@NotNull String itemId) {
        return InventoryRedisRepository.super.key(itemId);
    }

    @Override
    public @Nullable Long getStock(@NotNull String itemId) {
        final String stockStr = redisTemplate.opsForValue().get(key(itemId));
        if (stockStr == null) return null;
        return Long.parseLong(stockStr);
    }

    @Override
    public @NotNull Long decreaseStock(@NotNull String itemId, @NotNull Long quantity) {
        return redisTemplate.opsForValue().decrement(key(itemId), quantity);
    }

    @Override
    public @NotNull Boolean deleteStock(@NotNull String itemId) {
        return redisTemplate.delete(key(itemId));
    }

    @Override
    public @NotNull Long setStock(@NotNull String itemId, @NotNull Long quantity) {
        redisTemplate.opsForValue().set(key(itemId), quantity.toString());
        return quantity;
    }
}
