package com.Luxa.inventory.controller;

import com.Luxa.inventory.model.Product;
import com.Luxa.inventory.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    private final ProductService productService;

    public DashboardController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Product> all = productService.findAll();
        model.addAttribute("totalProducts", all.size());
        model.addAttribute("lowStockCount", productService.countLowStock());
        model.addAttribute("outOfStockCount", all.stream().filter(p -> p.getQuantity() == 0).count());
        model.addAttribute("totalCategories", productService.findAllCategories().size());
        return "dashboard";
    }

    @GetMapping("/api/dashboard-stats")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> dashboardStats() {
        List<Product> all = productService.findAll();

        // Summary stats
        long totalProducts = all.size();
        long lowStock = productService.countLowStock();
        long outOfStock = all.stream().filter(p -> p.getQuantity() == 0).count();
        long healthy = totalProducts - lowStock - outOfStock;

        // Total inventory value
        BigDecimal totalValue = all.stream()
                .filter(p -> p.getPrice() != null)
                .map(p -> p.getPrice().multiply(BigDecimal.valueOf(p.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Category breakdown
        Map<String, Long> categoryCount = all.stream()
                .filter(p -> p.getCategory() != null && !p.getCategory().isBlank())
                .collect(Collectors.groupingBy(Product::getCategory, Collectors.counting()));

        // Stock status breakdown
        Map<String, Long> stockStatus = new LinkedHashMap<>();
        stockStatus.put("Healthy", healthy);
        stockStatus.put("Low Stock", lowStock);
        stockStatus.put("Out of Stock", outOfStock);

        // Top 5 low stock items
        List<Map<String, Object>> lowStockItems = productService.findLowStockProducts()
                .stream()
                .limit(5)
                .map(p -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("name", p.getName());
                    item.put("quantity", p.getQuantity());
                    item.put("minThreshold", p.getMinThreshold());
                    item.put("category", p.getCategory());
                    return item;
                })
                .collect(Collectors.toList());

        // Top 5 by quantity
        List<Map<String, Object>> topItems = all.stream()
                .sorted(Comparator.comparingInt(Product::getQuantity).reversed())
                .limit(5)
                .map(p -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("name", p.getName());
                    item.put("quantity", p.getQuantity());
                    item.put("category", p.getCategory());
                    return item;
                })
                .collect(Collectors.toList());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalProducts", totalProducts);
        result.put("lowStockCount", lowStock);
        result.put("outOfStockCount", outOfStock);
        result.put("healthyCount", healthy);
        result.put("totalValue", totalValue);
        result.put("totalCategories", categoryCount.size());
        result.put("categoryBreakdown", categoryCount);
        result.put("stockStatus", stockStatus);
        result.put("lowStockItems", lowStockItems);
        result.put("topItems", topItems);

        return ResponseEntity.ok(result);
    }
}
