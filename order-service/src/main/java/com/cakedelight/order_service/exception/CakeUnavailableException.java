package com.cakedelight.order_service.exception;

public class CakeUnavailableException extends RuntimeException {

    public CakeUnavailableException(String cakeName) {
        super("Cake '" + cakeName + "' is currently not available");
    }
}