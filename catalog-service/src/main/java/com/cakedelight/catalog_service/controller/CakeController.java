package com.cakedelight.catalog_service.controller;

import com.cakedelight.catalog_service.dto.CakeFilter;               // NEW
import com.cakedelight.catalog_service.dto.CakeRequest;
import com.cakedelight.catalog_service.dto.CakeResponse;
import com.cakedelight.catalog_service.dto.PagedResponse;            // NEW
import com.cakedelight.catalog_service.entity.CakeCategory;          // NEW
import com.cakedelight.catalog_service.service.CakeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;                     // NEW
import org.springframework.data.web.PageableDefault;                 // NEW
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;                                         // NEW
import java.net.URI;

@RestController
@RequestMapping("/api/cakes")
@RequiredArgsConstructor
public class CakeController {

    private final CakeService cakeService;

    @PostMapping
    public ResponseEntity<CakeResponse> createCake(@RequestBody CakeRequest request) {
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

    // NEW: replaces getAllCakes()
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
                                                   @RequestBody CakeRequest request) {
        return ResponseEntity.ok(cakeService.updateCake(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCake(@PathVariable Long id) {
        cakeService.deleteCake(id);
        return ResponseEntity.noContent().build();
    }
}