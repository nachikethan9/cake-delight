package com.cakedelight.rating_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ratingOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Cake Delight - Rating Service API")
                        .version("v1")
                        .description("Submit cake ratings and view average ratings per cake."));
    }
}