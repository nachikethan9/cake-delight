package com.cakedelight.catalog_service.config;

import com.cakedelight.catalog_service.entity.Cake;
import com.cakedelight.catalog_service.entity.CakeCategory;
import com.cakedelight.catalog_service.repository.CakeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "app.seed-data.enabled", havingValue = "true")
public class DataSeeder implements CommandLineRunner {

    private final CakeRepository cakeRepository;

    @Override
    public void run(String... args) {
        if (cakeRepository.count() > 0) {
            log.info("Catalog already has data - skipping seed");
            return;
        }

        List<Cake> cakes = List.of(
                cake("Chocolate Truffle", "Rich dark chocolate layered cake", CakeCategory.BIRTHDAY, "899.00", true),
                cake("Black Forest", "Cherries, cream and chocolate sponge", CakeCategory.BIRTHDAY, "749.00", true),
                cake("Red Velvet", "Classic red velvet with cream cheese frosting", CakeCategory.ANNIVERSARY, "1199.50", true),
                cake("Classic Wedding Tier", "Three-tier vanilla with fondant", CakeCategory.WEDDING, "4999.00", true),
                cake("Mango Mousse", "Light mango mousse pastry", CakeCategory.PASTRY, "149.00", true),
                cake("Blueberry Cheesecake", "Baked cheesecake with blueberry topping", CakeCategory.PASTRY, "199.00", false),
                cake("Chocolate Lava Cake", "Warm cake with a molten centre", CakeCategory.PASTRY, "179.00", true),
                cake("Chocolate Cupcake", "Moist chocolate cupcake with frosting", CakeCategory.CUPCAKE, "89.00", true),
                cake("Vanilla Cupcake", "Vanilla sponge with buttercream", CakeCategory.CUPCAKE, "79.00", true),
                cake("Photo Cake", "Custom printed photo cake", CakeCategory.CUSTOM, "1499.00", true)
        );

        cakeRepository.saveAll(cakes);
        log.info("Seeded {} cakes into the catalog", cakes.size());
    }

    private Cake cake(String name, String description, CakeCategory category, String price, boolean available) {
        return Cake.builder()
                .name(name)
                .description(description)
                .category(category)
                .price(new BigDecimal(price))
                .available(available)
                .build();
    }
}