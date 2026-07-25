package com.Luxa.inventory.service;

import com.Luxa.inventory.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class DashboardService {

    private final ProductRepository productRepository;

    public DashboardService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public DashboardStats getStats() {
        long totalProducts = productRepository.count();
        // Uses each product's own minThreshold, same as the row-level
        // isLowStock() check that colors products red in the UI. Previously
        // this used one global threshold, which could disagree with the
        // per-product threshold and show a wrong count on the dashboard card.
        long lowStock = productRepository.countLowStockByOwnThreshold();
        long outOfStock = productRepository.countByQuantity(0);
        BigDecimal value = productRepository.sumInventoryValue();
        return new DashboardStats(totalProducts, lowStock, outOfStock, value);
    }

    public record DashboardStats(
            long totalProducts,
            long lowStockCount,
            long outOfStockCount,
            BigDecimal estimatedInventoryValue
    ) {}
}
