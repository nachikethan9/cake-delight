package com.cakedelight.order_service.service;

import com.cakedelight.order_service.client.CatalogCakeResponse;
import com.cakedelight.order_service.client.CatalogClient;
import com.cakedelight.order_service.dto.AddItemRequest;
import com.cakedelight.order_service.dto.BasketResponse;
import com.cakedelight.order_service.dto.UpdateQuantityRequest;
import com.cakedelight.order_service.entity.Basket;
import com.cakedelight.order_service.entity.BasketItem;
import com.cakedelight.order_service.exception.BasketItemNotFoundException;
import com.cakedelight.order_service.exception.CakeUnavailableException;
import com.cakedelight.order_service.exception.QuantityLimitExceededException;
import com.cakedelight.order_service.mapper.BasketMapper;
import com.cakedelight.order_service.repository.BasketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BasketService {

    static final int MAX_QUANTITY_PER_CAKE = 20;

    private final BasketRepository basketRepository;
    private final CatalogClient catalogClient;
    private final BasketMapper basketMapper;

    public BasketResponse getBasket(String userId) {
        return basketRepository.findByUserId(userId)
                .map(basketMapper::toResponse)
                .orElseGet(() -> basketMapper.emptyBasket(userId));
    }

    @Transactional
    public BasketResponse addItem(String userId, AddItemRequest request) {
        // 1. Ask Catalog for the real name, price and availability
        CatalogCakeResponse cake = catalogClient.getCake(request.cakeId());
        if (!cake.available()) {
            throw new CakeUnavailableException(cake.name());
        }

        // 2. Find the user's basket, or create one on first use
        Basket basket = basketRepository.findByUserId(userId)
                .orElseGet(() -> basketRepository.save(new Basket(userId)));

        // 3. Same cake again -> increase quantity; new cake -> add a line
        basket.findItem(cake.id()).ifPresentOrElse(
                existing -> {
                    int newQuantity = existing.getQuantity() + request.quantity();
                    checkQuantityLimit(newQuantity);
                    existing.setQuantity(newQuantity);
                    existing.setCakeName(cake.name());     // refresh with latest catalog data
                    existing.setUnitPrice(cake.price());
                },
                () -> basket.addItem(BasketItem.builder()
                        .cakeId(cake.id())
                        .cakeName(cake.name())
                        .unitPrice(cake.price())
                        .quantity(request.quantity())
                        .build())
        );

        log.info("Added cakeId={} qty={} to basket of user={}", cake.id(), request.quantity(), userId);
        return basketMapper.toResponse(basket);
    }

    @Transactional
    public BasketResponse updateItemQuantity(String userId, Long cakeId, UpdateQuantityRequest request) {
        Basket basket = findBasketOrThrow(userId, cakeId);
        BasketItem item = basket.findItem(cakeId)
                .orElseThrow(() -> new BasketItemNotFoundException(userId, cakeId));

        item.setQuantity(request.quantity());
        log.info("Updated cakeId={} to qty={} in basket of user={}", cakeId, request.quantity(), userId);
        return basketMapper.toResponse(basket);
    }

    @Transactional
    public BasketResponse removeItem(String userId, Long cakeId) {
        Basket basket = findBasketOrThrow(userId, cakeId);
        BasketItem item = basket.findItem(cakeId)
                .orElseThrow(() -> new BasketItemNotFoundException(userId, cakeId));

        basket.removeItem(item);
        log.info("Removed cakeId={} from basket of user={}", cakeId, userId);
        return basketMapper.toResponse(basket);
    }

    @Transactional
    public void clearBasket(String userId) {
        basketRepository.findByUserId(userId).ifPresent(basket -> {
            basket.clearItems();
            log.info("Cleared basket of user={}", userId);
        });
    }

    private Basket findBasketOrThrow(String userId, Long cakeId) {
        return basketRepository.findByUserId(userId)
                .orElseThrow(() -> new BasketItemNotFoundException(userId, cakeId));
    }

    private void checkQuantityLimit(int quantity) {
        if (quantity > MAX_QUANTITY_PER_CAKE) {
            throw new QuantityLimitExceededException(quantity, MAX_QUANTITY_PER_CAKE);
        }
    }
}