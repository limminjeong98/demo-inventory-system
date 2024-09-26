package com.demo.inventoryapp.common.controller;

import org.jetbrains.annotations.NotNull;

public record ApiErrorResponse(
        @NotNull String localMessage,
        @NotNull Long code
) {
}
