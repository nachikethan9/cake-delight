package com.cakedelight.order_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "basket_items",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_basket_item_cake",
                columnNames = {"basket_id", "cake_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BasketItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "basket_id", nullable = false)
    private Basket basket;

    @Column(name = "cake_id", nullable = false)
    private Long cakeId;

    @Column(name = "cake_name", nullable = false, length = 100)
    private String cakeName;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private int quantity;
}