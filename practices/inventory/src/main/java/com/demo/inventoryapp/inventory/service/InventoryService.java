package com.demo.inventoryapp.inventory.service;

import com.demo.inventoryapp.inventory.repository.InventoryJpaRepository;
import com.demo.inventoryapp.inventory.repository.entity.InventoryEntity;
import com.demo.inventoryapp.inventory.service.domain.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InventoryService {

    private final InventoryJpaRepository inventoryJpaRepository;

    public InventoryService(InventoryJpaRepository inventoryJpaRepository) {
        this.inventoryJpaRepository = inventoryJpaRepository;
    }

    public @Nullable Inventory findByItemId(@NotNull final String itemId) {
        return inventoryJpaRepository.findByItemId(itemId)
                .map(this::mapToDomain)
                .orElse(null);
    }

    private Inventory mapToDomain(InventoryEntity entity) {
        return new Inventory(entity.getItemId(), entity.getStock());
    }
}
