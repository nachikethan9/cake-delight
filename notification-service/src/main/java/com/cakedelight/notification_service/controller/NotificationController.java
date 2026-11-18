package com.cakedelight.notification_service.controller;

import com.cakedelight.notification_service.dto.NotificationResponse;
import com.cakedelight.notification_service.dto.PagedResponse;
import com.cakedelight.notification_service.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "In-app notification inbox and delivery status")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "In-app inbox: a user's notifications (newest first)")
    @GetMapping
    public ResponseEntity<PagedResponse<NotificationResponse>> getForUser(
            @RequestParam String userId,
            @ParameterObject
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(notificationService.getNotificationsForUser(userId, pageable));
    }

    @Operation(summary = "Notifications and delivery status for an order")
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<NotificationResponse>> getForOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(notificationService.getNotificationsForOrder(orderId));
    }
}