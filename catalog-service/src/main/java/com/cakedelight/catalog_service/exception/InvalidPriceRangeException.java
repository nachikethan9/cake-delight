package com.cakedelight.catalog_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.math.BigDecimal;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidPriceRangeException extends RuntimeException {

    public InvalidPriceRangeException(BigDecimal minPrice, BigDecimal maxPrice) {
        super("minPrice (" + minPrice + ") cannot be greater than maxPrice (" + maxPrice + ")");
    }
}