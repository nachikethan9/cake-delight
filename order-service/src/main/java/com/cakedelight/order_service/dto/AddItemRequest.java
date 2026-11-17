package com.cakedelight.order_service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AddItemRequest(

        @NotNull(message = "Cake ID is required")
        @Positive(message = "Cake ID must be a positive number")
        Long cakeId,

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        @Max(value = 20, message = "Quantity cannot exceed 20")
        Integer quantity
) {
}