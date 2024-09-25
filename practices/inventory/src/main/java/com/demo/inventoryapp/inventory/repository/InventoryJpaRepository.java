package com.demo.inventoryapp.inventory.repository;

import com.demo.inventoryapp.inventory.repository.entity.InventoryEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface InventoryJpaRepository {
    @NotNull
    Optional<InventoryEntity> findByItemId(@NotNull String itemId);

    @NotNull
    Integer decreaseStock(@NotNull String itemId, @NotNull Long quantity);

    @NotNull
    InventoryEntity save(@NotNull InventoryEntity inventoryEntity);
}
