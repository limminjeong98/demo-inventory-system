package com.demo.inventoryapp.inventory.service.domain;

import org.jetbrains.annotations.NotNull;

public record Inventory(@NotNull String itemId, @NotNull Long stock) {
}
