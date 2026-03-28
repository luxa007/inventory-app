package com.Luxa.inventory.dto;

import com.Luxa.inventory.model.Product;

import java.math.BigDecimal;

public record ProductDto(
        Long id,
        String name,
        BigDecimal price,
        Integer quantity,
        String category
) {
    public static ProductDto fromEntity(Product p) {
        return new ProductDto(
                p.getId(),
                p.getName(),
                p.getPrice(),
                p.getQuantity(),
                p.getCategory()
        );
    }
}
