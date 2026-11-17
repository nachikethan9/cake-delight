package com.cakedelight.order_service.client;

import java.math.BigDecimal;

public record CatalogCakeResponse(
        Long id,
        String name,
        BigDecimal price,
        boolean available
) {
}