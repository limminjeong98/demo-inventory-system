package com.demo.inventoryapp.common.controller;

import com.demo.inventoryapp.inventory.controller.consts.ErrorCodes;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.jetbrains.annotations.Nullable;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public record ApiResponse<T>(
        @Nullable T data,
        @Nullable ApiErrorResponse error
) {

    public static <T> ApiResponse<T> just(T data) {
        return new ApiResponse<>(data, null);
    }

    public static <T> ApiResponse<T> fromErrorCodes(ErrorCodes errorCodes) {
        final ApiErrorResponse errorResponse = new ApiErrorResponse(errorCodes.message, errorCodes.code);
        return new ApiResponse<>(null, errorResponse);
    }
}
