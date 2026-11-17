package com.cakedelight.order_service.service;

import com.cakedelight.order_service.client.CatalogCakeResponse;
import com.cakedelight.order_service.client.CatalogClient;
import com.cakedelight.order_service.dto.*;
import com.cakedelight.order_service.entity.*;
import com.cakedelight.order_service.exception.CakeUnavailableException;
import com.cakedelight.order_service.exception.EmptyBasketException;
import com.cakedelight.order_service.exception.InvalidStatusTransitionException;
import com.cakedelight.order_service.exception.OrderNotFoundException;
import com.cakedelight.order_service.mapper.OrderMapper;
import com.cakedelight.order_service.repository.BasketRepository;
import com.cakedelight.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final BasketRepository basketRepository;
    private final CatalogClient catalogClient;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderResponse checkout(CheckoutRequest request) {
        String userId = request.userId().trim();

        // 1. The basket must exist and have items
        Basket basket = basketRepository.findByUserId(userId)
                .filter(b -> !b.getItems().isEmpty())
                .orElseThrow(() -> new EmptyBasketException(userId));

        // 2. Re-check every item with Catalog and build the order with CURRENT prices
        CustomerOrder order = new CustomerOrder(userId, request.customerEmail().trim());
        for (BasketItem basketItem : basket.getItems()) {
            CatalogCakeResponse cake = catalogClient.getCake(basketItem.getCakeId());

            if (!cake.available()) {
                throw new CakeUnavailableException(cake.name());
            }
            if (cake.price().compareTo(basketItem.getUnitPrice()) != 0) {
                log.info("Price changed for cakeId={}: basket={} current={}",
                        cake.id(), basketItem.getUnitPrice(), cake.price());
            }
            order.addItem(OrderItem.of(cake.id(), cake.name(), cake.price(), basketItem.getQuantity()));
        }

        // 3. Save the order and empty the basket - in the SAME transaction
        CustomerOrder saved = orderRepository.save(order);
        basket.clearItems();

        log.info("Order placed orderId={} userId={} items={} total={}",
                saved.getId(), userId, saved.getItems().size(), saved.getTotalAmount());

        // O4: publish "order completed" event here

        return orderMapper.toResponse(saved);
    }

    public OrderResponse getOrder(Long orderId) {
        return orderMapper.toResponse(findOrderWithItemsOrThrow(orderId));
    }

    public PagedResponse<OrderSummaryResponse> getOrdersForUser(String userId, Pageable pageable) {
        return PagedResponse.from(
                orderRepository.findByUserId(userId.trim(), pageable).map(orderMapper::toSummary));
    }

    @Transactional
    public OrderResponse updateStatus(Long orderId, UpdateStatusRequest request) {
        CustomerOrder order = findOrderWithItemsOrThrow(orderId);
        OrderStatus current = order.getStatus();
        OrderStatus next = request.status();

        if (!current.canTransitionTo(next)) {
            throw new InvalidStatusTransitionException(orderId, current, next);
        }

        order.setStatus(next);
        log.info("Order {} status {} -> {}", orderId, current, next);
        return orderMapper.toResponse(order);
    }

    private CustomerOrder findOrderWithItemsOrThrow(Long orderId) {
        return orderRepository.findWithItemsById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }
}