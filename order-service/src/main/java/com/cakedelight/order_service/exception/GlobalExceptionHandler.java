package com.cakedelight.order_service.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.core.PropertyReferenceException;                 // NEW (O3)
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;          // NEW (O3)
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

    // ---------- 404 Not Found ----------

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

    @ExceptionHandler(OrderNotFoundException.class)                                // NEW (O3)
    public ProblemDetail handleOrderNotFound(OrderNotFoundException ex) {
        log.warn(ex.getMessage());
        return problem(HttpStatus.NOT_FOUND, "Order not found", ex.getMessage());
    }

    // ---------- 400 Bad Request ----------

    @ExceptionHandler(QuantityLimitExceededException.class)
    public ProblemDetail handleQuantityLimit(QuantityLimitExceededException ex) {
        log.warn(ex.getMessage());
        return problem(HttpStatus.BAD_REQUEST, "Quantity limit exceeded", ex.getMessage());
    }

    @ExceptionHandler(EmptyBasketException.class)                                  // NEW (O3)
    public ProblemDetail handleEmptyBasket(EmptyBasketException ex) {
        log.warn(ex.getMessage());
        return problem(HttpStatus.BAD_REQUEST, "Empty basket", ex.getMessage());
    }

    @ExceptionHandler(PropertyReferenceException.class)                            // NEW (O3)
    public ProblemDetail handleInvalidSort(PropertyReferenceException ex) {
        log.warn("Invalid sort property: {}", ex.getPropertyName());
        return problem(HttpStatus.BAD_REQUEST, "Invalid sort property",
                "Cannot sort by '" + ex.getPropertyName() + "'");
    }

    // ---------- 409 Conflict ----------

    @ExceptionHandler(CakeUnavailableException.class)
    public ProblemDetail handleCakeUnavailable(CakeUnavailableException ex) {
        log.warn(ex.getMessage());
        return problem(HttpStatus.CONFLICT, "Cake unavailable", ex.getMessage());
    }

    @ExceptionHandler(InvalidStatusTransitionException.class)                      // NEW (O3)
    public ProblemDetail handleInvalidTransition(InvalidStatusTransitionException ex) {
        log.warn(ex.getMessage());
        return problem(HttpStatus.CONFLICT, "Invalid status transition", ex.getMessage());
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)               // NEW (O3)
    public ProblemDetail handleOptimisticLock(ObjectOptimisticLockingFailureException ex) {
        log.warn("Concurrent modification: {}", ex.getMessage());
        return problem(HttpStatus.CONFLICT, "Concurrent update",
                "The order was modified by another request. Please reload and try again.");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrity(DataIntegrityViolationException ex) {
        log.warn("Data integrity violation: {}", ex.getMostSpecificCause().getMessage());
        return problem(HttpStatus.CONFLICT, "Conflict",
                "The request conflicts with existing data. Please retry.");
    }

    // ---------- 503 Service Unavailable ----------

    @ExceptionHandler(CatalogServiceUnavailableException.class)
    public ProblemDetail handleCatalogUnavailable(CatalogServiceUnavailableException ex) {
        log.error("Catalog dependency failure: {}", ex.getMessage());
        return problem(HttpStatus.SERVICE_UNAVAILABLE, "Catalog service unavailable", ex.getMessage());
    }

    // ---------- 500 Internal Server Error (safety net) ----------

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex) {
        log.error("Unexpected error", ex);
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error",
                "An unexpected error occurred. Please try again later.");
    }

    // ---------- 400 Validation (@Valid) ----------

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