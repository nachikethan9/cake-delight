package com.cakedelight.order_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CheckoutRequest(

        @NotBlank(message = "User ID is required")
        @Size(max = 100, message = "User ID must be at most 100 characters")
        String userId,

        @NotBlank(message = "Customer email is required")
        @Email(message = "Customer email must be a valid email address")
        @Size(max = 150, message = "Customer email must be at most 150 characters")
        String customerEmail
) {
}