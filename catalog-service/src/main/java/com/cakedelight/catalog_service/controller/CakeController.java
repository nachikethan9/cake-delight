package com.cakedelight.catalog_service.controller;

import com.cakedelight.catalog_service.dto.CakeRequest;
import com.cakedelight.catalog_service.dto.CakeResponse;
import com.cakedelight.catalog_service.service.CakeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<CakeResponse>> getAllCakes() {
        return ResponseEntity.ok(cakeService.getAllCakes());
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