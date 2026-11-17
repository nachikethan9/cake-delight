package com.cakedelight.rating_service.exception;

public class RatingNotFoundException extends RuntimeException {

    public RatingNotFoundException(Long id) {
        super("Rating not found with id: " + id);
    }
}