package com.Luxa.inventory.service;

import com.Luxa.inventory.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class DashboardService {

    private final ProductRepository productRepository;
    private final int lowStockThreshold;

    public DashboardService(
            ProductRepository productRepository,
            @Value("${app.low-stock-threshold:5}") int lowStockThreshold) {
        this.productRepository = productRepository;
        this.lowStockThreshold = lowStockThreshold;
    }

    @Transactional(readOnly = true)
    public DashboardStats getStats() {
        long totalProducts = productRepository.count();
        long lowStock = productRepository.countByQuantityBetween(1, lowStockThreshold);
        long outOfStock = productRepository.countByQuantity(0);
        BigDecimal value = productRepository.sumInventoryValue();
        return new DashboardStats(totalProducts, lowStock, outOfStock, value, lowStockThreshold);
    }

    public record DashboardStats(
            long totalProducts,
            long lowStockCount,
            long outOfStockCount,
            BigDecimal estimatedInventoryValue,
            int lowStockThreshold
    ) {}
}
