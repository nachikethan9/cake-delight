package com.cakedelight.catalog_service.exception;

import java.math.BigDecimal;

public class InvalidPriceRangeException extends RuntimeException {

    public InvalidPriceRangeException(BigDecimal minPrice, BigDecimal maxPrice) {
        super("minPrice (" + minPrice + ") cannot be greater than maxPrice (" + maxPrice + ")");
    }
}