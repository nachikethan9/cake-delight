package com.cakedelight.order_service.exception;

public class CakeNotFoundException extends RuntimeException {

    public CakeNotFoundException(Long cakeId) {
        super("Cake not found in catalog with id: " + cakeId);
    }
}