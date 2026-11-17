package com.cakedelight.catalog_service.service;

import com.cakedelight.catalog_service.dto.CakeRequest;
import com.cakedelight.catalog_service.dto.CakeResponse;
import com.cakedelight.catalog_service.entity.Cake;
import com.cakedelight.catalog_service.exception.CakeNotFoundException;
import com.cakedelight.catalog_service.mapper.CakeMapper;
import com.cakedelight.catalog_service.repository.CakeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public List<CakeResponse> getAllCakes() {
        return cakeRepository.findAll()
                .stream()
                .map(cakeMapper::toResponse)
                .toList();
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
}