package com.cakedelight.order_service.controller;

import com.cakedelight.order_service.dto.AddItemRequest;
import com.cakedelight.order_service.dto.BasketResponse;
import com.cakedelight.order_service.dto.UpdateQuantityRequest;
import com.cakedelight.order_service.service.BasketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/baskets/{userId}")
@RequiredArgsConstructor
@Tag(name = "Basket", description = "Shopping basket operations")
public class BasketController {

    private final BasketService basketService;

    @Operation(summary = "View the basket with line totals and grand total")
    @GetMapping
    public ResponseEntity<BasketResponse> getBasket(@PathVariable String userId) {
        return ResponseEntity.ok(basketService.getBasket(userId));
    }

    @Operation(summary = "Add a cake to the basket (adding the same cake again increases its quantity)")
    @PostMapping("/items")
    public ResponseEntity<BasketResponse> addItem(@PathVariable String userId,
                                                  @Valid @RequestBody AddItemRequest request) {
        return ResponseEntity.ok(basketService.addItem(userId, request));
    }

    @Operation(summary = "Change the quantity of a cake in the basket")
    @PutMapping("/items/{cakeId}")
    public ResponseEntity<BasketResponse> updateItemQuantity(@PathVariable String userId,
                                                             @PathVariable Long cakeId,
                                                             @Valid @RequestBody UpdateQuantityRequest request) {
        return ResponseEntity.ok(basketService.updateItemQuantity(userId, cakeId, request));
    }

    @Operation(summary = "Remove a cake from the basket")
    @DeleteMapping("/items/{cakeId}")
    public ResponseEntity<BasketResponse> removeItem(@PathVariable String userId,
                                                     @PathVariable Long cakeId) {
        return ResponseEntity.ok(basketService.removeItem(userId, cakeId));
    }

    @Operation(summary = "Empty the basket")
    @DeleteMapping
    public ResponseEntity<Void> clearBasket(@PathVariable String userId) {
        basketService.clearBasket(userId);
        return ResponseEntity.noContent().build();
    }
}