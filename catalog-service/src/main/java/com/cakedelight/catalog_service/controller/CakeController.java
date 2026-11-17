package com.cakedelight.catalog_service.controller;

import com.cakedelight.catalog_service.dto.CakeFilter;
import com.cakedelight.catalog_service.dto.CakeRequest;
import com.cakedelight.catalog_service.dto.CakeResponse;
import com.cakedelight.catalog_service.dto.PagedResponse;
import com.cakedelight.catalog_service.entity.CakeCategory;
import com.cakedelight.catalog_service.service.CakeService;
import jakarta.validation.Valid;                                     // NEW
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;

@RestController
@RequestMapping("/api/cakes")
@RequiredArgsConstructor
public class CakeController {

    private final CakeService cakeService;

    @PostMapping
    public ResponseEntity<CakeResponse> createCake(@Valid @RequestBody CakeRequest request) {   // NEW: @Valid
        CakeResponse created = cakeService.createCake(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CakeResponse> getCakeById(@PathVariable Long id) {
        return ResponseEntity.ok(cakeService.getCakeById(id));
    }

    @GetMapping
    public ResponseEntity<PagedResponse<CakeResponse>> searchCakes(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) CakeCategory category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean available,
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {

        CakeFilter filter = new CakeFilter(name, category, minPrice, maxPrice, available);
        return ResponseEntity.ok(cakeService.searchCakes(filter, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CakeResponse> updateCake(@PathVariable Long id,
                                                   @Valid @RequestBody CakeRequest request) {   // NEW: @Valid
        return ResponseEntity.ok(cakeService.updateCake(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCake(@PathVariable Long id) {
        cakeService.deleteCake(id);
        return ResponseEntity.noContent().build();
    }
}