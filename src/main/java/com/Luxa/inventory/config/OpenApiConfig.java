package com.Luxa.inventory.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI smartStockOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SmartStock AI — Inventory Management API")
                        .description("REST API for SmartStock AI, an intelligent inventory management system " +
                                "with AI-powered restock recommendations, barcode scanning, and analytics.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Tinotenda Luxa")
                                .url("https://github.com/luxa007/inventory-app")));
    }
}
