package com.cakedelight.catalog_service.exception;

public class CakeNotFoundException extends RuntimeException {

    public CakeNotFoundException(Long id) {
        super("Cake not found with id: " + id);
    }
}