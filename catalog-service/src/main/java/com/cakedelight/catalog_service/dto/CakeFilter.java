package com.cakedelight.catalog_service.dto;

import com.cakedelight.catalog_service.entity.CakeCategory;

import java.math.BigDecimal;

public record CakeFilter(
        String name,
        CakeCategory category,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        Boolean available
) {
}