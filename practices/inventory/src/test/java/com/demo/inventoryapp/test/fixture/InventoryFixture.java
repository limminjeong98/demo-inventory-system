package com.demo.inventoryapp.test.fixture;

import com.demo.inventoryapp.inventory.service.domain.Inventory;
import org.jetbrains.annotations.Nullable;

public class InventoryFixture {
    public static Inventory sampleInventory(
            @Nullable String itemId,
            @Nullable Long stock
    ) {
        String inventoryItemId = itemId;
        if (inventoryItemId == null) {
            inventoryItemId = "1";
        }

        Long inventoryStock = stock;
        if (inventoryStock == null) {
            inventoryStock = 100L;
        }

        return new Inventory(inventoryItemId, inventoryStock);
    }
}
