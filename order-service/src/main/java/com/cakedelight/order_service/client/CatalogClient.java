package com.cakedelight.order_service.client;

import com.cakedelight.order_service.exception.CakeNotFoundException;
import com.cakedelight.order_service.exception.CatalogServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
@RequiredArgsConstructor
@Slf4j
public class CatalogClient {

    private final RestClient catalogRestClient;

    public CatalogCakeResponse getCake(Long cakeId) {
        try {
            log.debug("Calling catalog-service for cakeId={}", cakeId);
            return catalogRestClient.get()
                    .uri("/api/cakes/{id}", cakeId)
                    .retrieve()
                    .body(CatalogCakeResponse.class);

        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new CakeNotFoundException(cakeId);
            }
            throw new CatalogServiceUnavailableException(
                    "Catalog service returned " + ex.getStatusCode() + " for cake " + cakeId, ex);

        } catch (ResourceAccessException ex) {
            log.error("Catalog service unreachable or timed out for cakeId={}", cakeId, ex);
            throw new CatalogServiceUnavailableException(
                    "Catalog service is currently unavailable. Please try again later.", ex);

        } catch (RestClientException ex) {
            log.error("Unexpected error calling catalog service for cakeId={}", cakeId, ex);
            throw new CatalogServiceUnavailableException(
                    "Could not get cake details from catalog service.", ex);
        }
    }
}