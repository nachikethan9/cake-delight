package com.cakedelight.order_service.mapper;

import com.cakedelight.order_service.dto.BasketItemResponse;
import com.cakedelight.order_service.dto.BasketResponse;
import com.cakedelight.order_service.entity.Basket;
import com.cakedelight.order_service.entity.BasketItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class BasketMapper {

    public BasketResponse toResponse(Basket basket) {
        List<BasketItemResponse> items = basket.getItems().stream()
                .map(this::toItemResponse)
                .toList();
        return buildResponse(basket.getUserId(), items);
    }

    public BasketResponse emptyBasket(String userId) {
        return buildResponse(userId, List.of());
    }

    private BasketItemResponse toItemResponse(BasketItem item) {
        BigDecimal lineTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        return new BasketItemResponse(
                item.getCakeId(),
                item.getCakeName(),
                item.getUnitPrice(),
                item.getQuantity(),
                lineTotal
        );
    }

    private BasketResponse buildResponse(String userId, List<BasketItemResponse> items) {
        BigDecimal totalAmount = items.stream()
                .map(BasketItemResponse::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalItems = items.stream()
                .mapToInt(BasketItemResponse::quantity)
                .sum();

        return new BasketResponse(userId, items, totalItems, totalAmount);
    }
}