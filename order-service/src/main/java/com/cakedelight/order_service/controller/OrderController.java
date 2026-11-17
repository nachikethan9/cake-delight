package com.cakedelight.order_service.controller;

import com.cakedelight.order_service.dto.*;
import com.cakedelight.order_service.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Checkout and order tracking")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Checkout the user's basket and create an order")
    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(@Valid @RequestBody CheckoutRequest request) {
        OrderResponse order = orderService.checkout(request);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/orders/{id}")
                .buildAndExpand(order.orderId())
                .toUri();
        return ResponseEntity.created(location).body(order);
    }

    @Operation(summary = "Get full order details")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrder(orderId));
    }

    @Operation(summary = "Order history for a user (newest first)")
    @GetMapping
    public ResponseEntity<PagedResponse<OrderSummaryResponse>> getOrdersForUser(
            @RequestParam String userId,
            @ParameterObject
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(orderService.getOrdersForUser(userId, pageable));
    }

    @Operation(summary = "Update order status (only valid transitions are allowed)")
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateStatus(@PathVariable Long orderId,
                                                      @Valid @RequestBody UpdateStatusRequest request) {
        return ResponseEntity.ok(orderService.updateStatus(orderId, request));
    }
}