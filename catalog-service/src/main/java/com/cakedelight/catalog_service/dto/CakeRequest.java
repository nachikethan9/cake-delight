package com.cakedelight.catalog_service.dto;

import com.cakedelight.catalog_service.entity.CakeCategory;

import java.math.BigDecimal;

public record CakeRequest(
        String name,
        String description,
        CakeCategory category,
        BigDecimal price,
        Boolean available,
        String imageUrl
) {
}