package com.cakedelight.catalog_service.dto;

import com.cakedelight.catalog_service.entity.CakeCategory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CakeResponse(
        Long id,
        String name,
        String description,
        CakeCategory category,
        BigDecimal price,
        boolean available,
        String imageUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}