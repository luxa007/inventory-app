package com.Luxa.inventory.controller;

import com.Luxa.inventory.model.Product;
import com.Luxa.inventory.service.ProductService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class RestockAdvisorController {

    private final ProductService productService;
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String AI_SERVICE_URL = "https://smartstock-ai-service-f2jk.onrender.com/restock-advice";

    public RestockAdvisorController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/restock-advice")
    public ResponseEntity<Map<String, Object>> getRestockAdvice() {
        List<Product> allProducts = productService.findAll();

        List<Map<String, Object>> inventorySnapshot = allProducts.stream().map(p -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name", p.getName());
            item.put("category", p.getCategory());
            item.put("quantity", p.getQuantity());
            item.put("min_threshold", p.getMinThreshold());
            item.put("price", p.getPrice());
            item.put("sales_velocity", p.getSalesVelocity());
            item.put("is_low_stock", p.isLowStock());
            return item;
        }).collect(Collectors.toList());

        long lowStockCount = allProducts.stream().filter(Product::isLowStock).count();
        long outOfStockCount = allProducts.stream().filter(p -> p.getQuantity() == 0).count();

        Map<String, Object> payload = new HashMap<>();
        payload.put("inventory", inventorySnapshot);
        payload.put("total_products", allProducts.size());
        payload.put("low_stock_count", lowStockCount);
        payload.put("out_of_stock_count", outOfStockCount);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            ResponseEntity<Map> response = restTemplate.exchange(AI_SERVICE_URL, HttpMethod.POST, request, Map.class);
            return ResponseEntity.ok((Map<String, Object>) response.getBody());
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "AI service unavailable: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
        }
    }
}
