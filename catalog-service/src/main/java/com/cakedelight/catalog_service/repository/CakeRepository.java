package com.cakedelight.catalog_service.repository;

import com.cakedelight.catalog_service.entity.Cake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CakeRepository extends JpaRepository<Cake, Long> {
}