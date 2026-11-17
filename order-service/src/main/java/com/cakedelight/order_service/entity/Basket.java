package com.cakedelight.order_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "baskets")
@Getter
@Setter
@NoArgsConstructor
public class Basket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true, length = 100)
    private String userId;

    @OneToMany(mappedBy = "basket", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BasketItem> items = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Basket(String userId) {
        this.userId = userId;
    }

    public Optional<BasketItem> findItem(Long cakeId) {
        return items.stream()
                .filter(item -> item.getCakeId().equals(cakeId))
                .findFirst();
    }

    public void addItem(BasketItem item) {
        item.setBasket(this);
        items.add(item);
    }

    public void removeItem(BasketItem item) {
        items.remove(item);
        item.setBasket(null);
    }

    public void clearItems() {
        items.forEach(item -> item.setBasket(null));
        items.clear();
    }
}