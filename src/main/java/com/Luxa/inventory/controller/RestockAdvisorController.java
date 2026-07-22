package com.Luxa.inventory.controller;

import com.Luxa.inventory.model.Product;
import com.Luxa.inventory.service.ProductService;
import com.Luxa.inventory.service.RestockAdvisorService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class RestockAdvisorController {

    private final ProductService productService;
    private final RestockAdvisorService restockAdvisorService;

    public RestockAdvisorController(ProductService productService,
                                    RestockAdvisorService restockAdvisorService) {
        this.productService = productService;
        this.restockAdvisorService = restockAdvisorService;
    }

    @GetMapping("/restock-advice")
    public ResponseEntity<Map<String, Object>> getRestockAdvice() {
        List<Product> products = productService.findAll();
        Map<String, Object> advice = restockAdvisorService.getAdvice(products);
        return ResponseEntity.ok(advice);
    }
}
