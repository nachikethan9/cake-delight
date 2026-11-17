package com.cakedelight.catalog_service.mapper;

import com.cakedelight.catalog_service.dto.CakeRequest;
import com.cakedelight.catalog_service.dto.CakeResponse;
import com.cakedelight.catalog_service.entity.Cake;
import org.springframework.stereotype.Component;

@Component
public class CakeMapper {

    public Cake toEntity(CakeRequest request) {
        return Cake.builder()
                .name(request.name())
                .description(request.description())
                .category(request.category())
                .price(request.price())
                .available(request.available() == null || request.available())
                .imageUrl(request.imageUrl())
                .build();
    }

    public void updateEntity(Cake cake, CakeRequest request) {
        cake.setName(request.name());
        cake.setDescription(request.description());
        cake.setCategory(request.category());
        cake.setPrice(request.price());
        cake.setAvailable(request.available() == null || request.available());
        cake.setImageUrl(request.imageUrl());
    }

    public CakeResponse toResponse(Cake cake) {
        return new CakeResponse(
                cake.getId(),
                cake.getName(),
                cake.getDescription(),
                cake.getCategory(),
                cake.getPrice(),
                cake.isAvailable(),
                cake.getImageUrl(),
                cake.getCreatedAt(),
                cake.getUpdatedAt()
        );
    }
}