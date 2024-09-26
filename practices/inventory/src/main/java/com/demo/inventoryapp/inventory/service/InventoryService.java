package com.demo.inventoryapp.inventory.service;

import com.demo.inventoryapp.inventory.repository.InventoryJpaRepository;
import com.demo.inventoryapp.inventory.repository.entity.InventoryEntity;
import com.demo.inventoryapp.inventory.service.domain.Inventory;
import com.demo.inventoryapp.inventory.service.exception.InsufficientStockException;
import com.demo.inventoryapp.inventory.service.exception.InvalidDecreaseQuantityException;
import com.demo.inventoryapp.inventory.service.exception.InvalidStockException;
import com.demo.inventoryapp.inventory.service.exception.ItemNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
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

    public @NotNull Inventory decreaseByItemId(@NotNull final String itemId, @NotNull final Long quantity) {
        // 차감할 재고의 수량은 0 이상
        if (quantity < 0) throw new InvalidDecreaseQuantityException();

        // itemId에 해당하는 엔티티 없음
        final InventoryEntity inventoryEntity = inventoryJpaRepository.findByItemId(itemId).orElseThrow(ItemNotFoundException::new);

        // 재고 부족
        if (inventoryEntity.getStock() < quantity) throw new InsufficientStockException();

        // 차감하려는 시점에 재고가 사라지는 등의 이슈로 차감에 실패한다면
        final Integer updateCount = inventoryJpaRepository.decreaseStock(itemId, quantity);
        if (updateCount == 0) throw new ItemNotFoundException();

        final InventoryEntity updatedEntity = inventoryJpaRepository.findByItemId(itemId).orElseThrow(ItemNotFoundException::new);
        return mapToDomain(updatedEntity);
    }


    public @NotNull Inventory updateStock(@NotNull final String itemId, @NotNull Long newStock) {
        // 업데이트하려는 재고 수량은 0 이상
        if (newStock < 0) throw new InvalidStockException();

        // itemId에 해당하는 엔티티 없음
        final InventoryEntity inventoryEntity = inventoryJpaRepository.findByItemId(itemId).orElseThrow(ItemNotFoundException::new);

        inventoryEntity.setStock(newStock);
        return mapToDomain(inventoryJpaRepository.save(inventoryEntity));
    }


    private Inventory mapToDomain(InventoryEntity entity) {
        return new Inventory(entity.getItemId(), entity.getStock());
    }
}
