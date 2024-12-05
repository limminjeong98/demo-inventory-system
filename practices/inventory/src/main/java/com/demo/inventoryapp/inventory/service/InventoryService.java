package com.demo.inventoryapp.inventory.service;

import com.demo.inventoryapp.inventory.service.domain.Inventory;
import com.demo.inventoryapp.inventory.service.exception.InsufficientStockException;
import com.demo.inventoryapp.inventory.service.exception.InvalidDecreaseQuantityException;
import com.demo.inventoryapp.inventory.service.exception.InvalidStockException;
import com.demo.inventoryapp.inventory.service.exception.ItemNotFoundException;
import com.demo.inventoryapp.inventory.service.persistence.InventoryPersistenceAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {

    private final InventoryPersistenceAdapter inventoryAdapter;

    public InventoryService(InventoryPersistenceAdapter inventoryAdapter) {
        this.inventoryAdapter = inventoryAdapter;
    }

    public @Nullable Inventory findByItemId(@NotNull final String itemId) {
        return inventoryAdapter.findByItemId(itemId);
    }

    @Transactional
    public @NotNull Inventory decreaseByItemId(@NotNull final String itemId, @NotNull final Long quantity) {
        // 차감할 재고의 수량은 0 이상
        if (quantity < 0) throw new InvalidDecreaseQuantityException();

        // itemId에 해당하는 엔티티 없음
        final Inventory inventory = inventoryAdapter.findByItemId(itemId);
        if (inventory == null) {
            throw new ItemNotFoundException();
        }

        // 재고 부족
        if (inventory.getStock() < quantity) throw new InsufficientStockException();

        // 차감하려는 시점에 재고가 사라지는 등의 이슈로 차감에 실패한다면
        final Inventory updatedInventory = inventoryAdapter.decreaseStock(itemId, quantity);
        if (updatedInventory == null) throw new ItemNotFoundException();

        return updatedInventory;
    }


    public @NotNull Inventory updateStock(@NotNull final String itemId, @NotNull Long newStock) {
        // 업데이트하려는 재고 수량은 0 이상
        if (newStock < 0) throw new InvalidStockException();

        // itemId에 해당하는 엔티티 없음
        final Inventory inventory = inventoryAdapter.findByItemId(itemId);
        if (inventory == null) {
            throw new ItemNotFoundException();
        }

        inventory.setStock(newStock);
        return inventoryAdapter.save(inventory);
    }
}
