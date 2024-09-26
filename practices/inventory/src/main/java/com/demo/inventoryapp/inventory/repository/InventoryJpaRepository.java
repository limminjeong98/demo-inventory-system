package com.demo.inventoryapp.inventory.repository;

import com.demo.inventoryapp.inventory.repository.entity.InventoryEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryJpaRepository extends JpaRepository<InventoryEntity, Long> {
    @NotNull
    Optional<InventoryEntity> findByItemId(@NotNull String itemId);

    @NotNull
    default Integer decreaseStock(@NotNull String itemId, @NotNull Long quantity) {
        return -1;
    }
}
