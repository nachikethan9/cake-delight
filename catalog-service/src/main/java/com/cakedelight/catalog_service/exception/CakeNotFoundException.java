package com.cakedelight.catalog_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CakeNotFoundException extends RuntimeException {

    public CakeNotFoundException(Long id) {
        super("Cake not found with id: " + id);
    }
}