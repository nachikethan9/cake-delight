package com.cakedelight.order_service.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CakeNotFoundException.class)
    public ProblemDetail handleCakeNotFound(CakeNotFoundException ex) {
        log.warn(ex.getMessage());
        return problem(HttpStatus.NOT_FOUND, "Cake not found", ex.getMessage());
    }

    @ExceptionHandler(BasketItemNotFoundException.class)
    public ProblemDetail handleBasketItemNotFound(BasketItemNotFoundException ex) {
        log.warn(ex.getMessage());
        return problem(HttpStatus.NOT_FOUND, "Basket item not found", ex.getMessage());
    }

    @ExceptionHandler(CakeUnavailableException.class)
    public ProblemDetail handleCakeUnavailable(CakeUnavailableException ex) {
        log.warn(ex.getMessage());
        return problem(HttpStatus.CONFLICT, "Cake unavailable", ex.getMessage());
    }

    @ExceptionHandler(QuantityLimitExceededException.class)
    public ProblemDetail handleQuantityLimit(QuantityLimitExceededException ex) {
        log.warn(ex.getMessage());
        return problem(HttpStatus.BAD_REQUEST, "Quantity limit exceeded", ex.getMessage());
    }

    @ExceptionHandler(CatalogServiceUnavailableException.class)
    public ProblemDetail handleCatalogUnavailable(CatalogServiceUnavailableException ex) {
        log.error("Catalog dependency failure: {}", ex.getMessage());
        return problem(HttpStatus.SERVICE_UNAVAILABLE, "Catalog service unavailable", ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrity(DataIntegrityViolationException ex) {
        log.warn("Data integrity violation: {}", ex.getMostSpecificCause().getMessage());
        return problem(HttpStatus.CONFLICT, "Conflict",
                "The request conflicts with existing data. Please retry.");
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex) {
        log.error("Unexpected error", ex);
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error",
                "An unexpected error occurred. Please try again later.");
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.putIfAbsent(error.getField(), error.getDefaultMessage()));
        log.warn("Validation failed: {}", errors);

        ProblemDetail body = problem(HttpStatus.BAD_REQUEST, "Validation failed",
                "One or more fields are invalid");
        body.setProperty("errors", errors);
        return ResponseEntity.badRequest().body(body);
    }

    private ProblemDetail problem(HttpStatus status, String title, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}