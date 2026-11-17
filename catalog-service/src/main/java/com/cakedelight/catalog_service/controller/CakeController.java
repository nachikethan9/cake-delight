package com.cakedelight.catalog_service.controller;

import com.cakedelight.catalog_service.dto.CakeFilter;
import com.cakedelight.catalog_service.dto.CakeRequest;
import com.cakedelight.catalog_service.dto.CakeResponse;
import com.cakedelight.catalog_service.dto.PagedResponse;
import com.cakedelight.catalog_service.entity.CakeCategory;
import com.cakedelight.catalog_service.service.CakeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
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
@Tag(name = "Cakes", description = "Cake catalog operations")
public class CakeController {

    private final CakeService cakeService;

    @Operation(summary = "Create a new cake")
    @PostMapping
    public ResponseEntity<CakeResponse> createCake(@Valid @RequestBody CakeRequest request) {
        CakeResponse created = cakeService.createCake(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Get a cake by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<CakeResponse> getCakeById(@PathVariable Long id) {
        return ResponseEntity.ok(cakeService.getCakeById(id));
    }

    @Operation(summary = "Search cakes",
            description = "Filter by name (partial, case-insensitive), category, price range and availability. Supports paging and sorting.")
    @GetMapping
    public ResponseEntity<PagedResponse<CakeResponse>> searchCakes(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) CakeCategory category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean available,
            @ParameterObject @PageableDefault(size = 10, sort = "name") Pageable pageable) {

        CakeFilter filter = new CakeFilter(name, category, minPrice, maxPrice, available);
        return ResponseEntity.ok(cakeService.searchCakes(filter, pageable));
    }

    @Operation(summary = "Update an existing cake")
    @PutMapping("/{id}")
    public ResponseEntity<CakeResponse> updateCake(@PathVariable Long id,
                                                   @Valid @RequestBody CakeRequest request) {
        return ResponseEntity.ok(cakeService.updateCake(id, request));
    }

    @Operation(summary = "Delete a cake")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCake(@PathVariable Long id) {
        cakeService.deleteCake(id);
        return ResponseEntity.noContent().build();
    }
}