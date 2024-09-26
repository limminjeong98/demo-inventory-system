package com.demo.inventoryapp.inventory.repository.entity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InventoryEntity {
    private @Nullable
    final Long id;
    private @NotNull
    final String itemId;
    private @NotNull Long stock;


    public InventoryEntity(@Nullable Long id, @NotNull String itemId, @NotNull Long stock) {
        this.id = id;
        this.itemId = itemId;
        this.stock = stock;
    }

    public @Nullable Long getId() {
        return id;
    }

    public @NotNull String getItemId() {
        return itemId;
    }

    public @NotNull Long getStock() {
        return stock;
    }

    public void setStock(@NotNull Long stock) {
        this.stock = stock;
    }
}
