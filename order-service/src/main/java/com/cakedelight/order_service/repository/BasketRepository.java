package com.cakedelight.order_service.repository;

import com.cakedelight.order_service.entity.Basket;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BasketRepository extends JpaRepository<Basket, Long> {

    @EntityGraph(attributePaths = "items")
    Optional<Basket> findByUserId(String userId);
}