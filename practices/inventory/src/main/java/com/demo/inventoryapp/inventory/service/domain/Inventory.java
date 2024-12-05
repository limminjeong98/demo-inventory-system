package com.demo.inventoryapp.inventory.service.domain;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Inventory {
    private @Nullable Long id;
    private @NotNull String itemId;
    private @NotNull Long stock;

    public Inventory(@Nullable Long id, @NotNull String itemId, @NotNull Long stock) {
        this.id = id;
        this.itemId = itemId;
        this.stock = stock;
    }

    public @Nullable Long getId() {
        return id;
    }

    public void setId(@NotNull Long id) {
        this.id = id;
    }

    public @NotNull String getItemId() {
        return itemId;
    }

    public void setItemId(@NotNull String itemId) {
        this.itemId = itemId;
    }

    public @NotNull Long getStock() {
        return stock;
    }

    public void setStock(@NotNull Long stock) {
        this.stock = stock;
    }
}
