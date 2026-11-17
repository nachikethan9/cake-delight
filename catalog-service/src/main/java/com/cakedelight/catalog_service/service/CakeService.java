package com.cakedelight.catalog_service.service;

import com.cakedelight.catalog_service.dto.CakeFilter;                          // NEW
import com.cakedelight.catalog_service.dto.CakeRequest;
import com.cakedelight.catalog_service.dto.CakeResponse;
import com.cakedelight.catalog_service.dto.PagedResponse;                       // NEW
import com.cakedelight.catalog_service.entity.Cake;
import com.cakedelight.catalog_service.exception.CakeNotFoundException;
import com.cakedelight.catalog_service.exception.InvalidPriceRangeException;    // NEW
import com.cakedelight.catalog_service.mapper.CakeMapper;
import com.cakedelight.catalog_service.repository.CakeRepository;
import com.cakedelight.catalog_service.repository.CakeSpecifications;           // NEW
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;                                    // NEW
import org.springframework.data.domain.Pageable;                                // NEW
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CakeService {

    private final CakeRepository cakeRepository;
    private final CakeMapper cakeMapper;

    @Transactional
    public CakeResponse createCake(CakeRequest request) {
        Cake saved = cakeRepository.save(cakeMapper.toEntity(request));
        log.info("Created cake id={} name={}", saved.getId(), saved.getName());
        return cakeMapper.toResponse(saved);
    }

    public CakeResponse getCakeById(Long id) {
        return cakeMapper.toResponse(findCakeOrThrow(id));
    }

    // NEW: replaces getAllCakes()
    public PagedResponse<CakeResponse> searchCakes(CakeFilter filter, Pageable pageable) {
        validatePriceRange(filter);
        Page<CakeResponse> page = cakeRepository
                .findAll(CakeSpecifications.withFilter(filter), pageable)
                .map(cakeMapper::toResponse);
        return PagedResponse.from(page);
    }

    @Transactional
    public CakeResponse updateCake(Long id, CakeRequest request) {
        Cake cake = findCakeOrThrow(id);
        cakeMapper.updateEntity(cake, request);
        log.info("Updated cake id={}", id);
        return cakeMapper.toResponse(cake);
    }

    @Transactional
    public void deleteCake(Long id) {
        if (!cakeRepository.existsById(id)) {
            throw new CakeNotFoundException(id);
        }
        cakeRepository.deleteById(id);
        log.info("Deleted cake id={}", id);
    }

    private Cake findCakeOrThrow(Long id) {
        return cakeRepository.findById(id)
                .orElseThrow(() -> new CakeNotFoundException(id));
    }

    // NEW
    private void validatePriceRange(CakeFilter filter) {
        if (filter.minPrice() != null && filter.maxPrice() != null
                && filter.minPrice().compareTo(filter.maxPrice()) > 0) {
            throw new InvalidPriceRangeException(filter.minPrice(), filter.maxPrice());
        }
    }
}