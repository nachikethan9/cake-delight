package com.cakedelight.order_service.repository;

import com.cakedelight.order_service.entity.CustomerOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<CustomerOrder, Long> {

    @EntityGraph(attributePaths = "items")
    Optional<CustomerOrder> findWithItemsById(Long id);

    Page<CustomerOrder> findByUserId(String userId, Pageable pageable);
}