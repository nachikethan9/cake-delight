package com.cakedelight.order_service.exception;

public class CatalogServiceUnavailableException extends RuntimeException {

    public CatalogServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}