package com.Luxa.inventory.controller;

import com.Luxa.inventory.model.SaleTransaction;
import com.Luxa.inventory.model.StockMovement;
import com.Luxa.inventory.service.SaleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping("/sales")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> recordSale(@RequestBody Map<String, Object> request,
                                         Authentication auth) {
        try {
            Long productId = Long.valueOf(request.get("productId").toString());
            int quantity = Integer.parseInt(request.get("quantity").toString());
            SaleTransaction sale = saleService.recordSale(productId, quantity, auth.getName());
            return ResponseEntity.ok(Map.of(
                "message", "Sale recorded successfully",
                "saleId", sale.getId(),
                "totalAmount", sale.getTotalAmount()
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/stock/receive")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> receiveStock(@RequestBody Map<String, Object> request,
                                           Authentication auth) {
        Long productId = Long.valueOf(request.get("productId").toString());
        int quantity = Integer.parseInt(request.get("quantity").toString());
        String note = request.getOrDefault("note", "Stock received").toString();
        saleService.receiveStock(productId, quantity, auth.getName(), note);
        return ResponseEntity.ok(Map.of("message", "Stock received successfully"));
    }

    @GetMapping("/sales/product/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN','VIEWER')")
    public ResponseEntity<List<SaleTransaction>> getSalesForProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(saleService.getSalesForProduct(productId));
    }

    @GetMapping("/stock/movements")
    @PreAuthorize("hasAnyRole('ADMIN','VIEWER')")
    public ResponseEntity<List<StockMovement>> getRecentMovements() {
        return ResponseEntity.ok(saleService.getRecentMovements());
    }

    @GetMapping("/analytics/revenue")
    @PreAuthorize("hasAnyRole('ADMIN','VIEWER')")
    public ResponseEntity<?> getRevenue() {
        return ResponseEntity.ok(Map.of(
            "todayRevenue", saleService.getTodayRevenue(),
            "monthlyRevenue", saleService.getMonthlyRevenue()
        ));
    }
}
