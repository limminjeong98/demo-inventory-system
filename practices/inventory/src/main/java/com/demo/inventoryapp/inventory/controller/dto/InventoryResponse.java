package com.demo.inventoryapp.inventory.controller.dto;

import com.demo.inventoryapp.inventory.service.domain.Inventory;
import org.jetbrains.annotations.NotNull;

public record InventoryResponse(
        @NotNull String itemId,
        @NotNull Long stock
) {
    public static InventoryResponse fromDomain(@NotNull final Inventory inventory) {
        return new InventoryResponse(inventory.itemId(), inventory.stock());
    }
}
