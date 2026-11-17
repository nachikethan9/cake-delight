package com.cakedelight.catalog_service.dto;

import com.cakedelight.catalog_service.entity.CakeCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CakeRequest(

        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must be at most 100 characters")
        String name,

        @Size(max = 1000, message = "Description must be at most 1000 characters")
        String description,

        @NotNull(message = "Category is required")
        CakeCategory category,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        @Digits(integer = 8, fraction = 2, message = "Price can have at most 8 digits before and 2 after the decimal point")
        BigDecimal price,

        Boolean available,

        @Size(max = 500, message = "Image URL must be at most 500 characters")
        String imageUrl
) {
}