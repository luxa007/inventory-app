package com.Luxa.inventory.controller;

import com.Luxa.inventory.service.ProductService;
import com.Luxa.inventory.service.SaleService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SalesMvcController {

    private final SaleService saleService;
    private final ProductService productService;

    public SalesMvcController(SaleService saleService, ProductService productService) {
        this.saleService = saleService;
        this.productService = productService;
    }

    @GetMapping("/sales")
    @PreAuthorize("hasRole('ADMIN')")
    public String salesPage(Model model) {
        model.addAttribute("products", productService.findAll());
        model.addAttribute("recentSales", saleService.getRecentSales());
        return "sales";
    }

    @PostMapping("/sales")
    @PreAuthorize("hasRole('ADMIN')")
    public String recordSale(@RequestParam Long productId,
                              @RequestParam Integer quantity,
                              RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        try {
            saleService.recordSale(productId, quantity, auth.getName());
            redirectAttributes.addFlashAttribute("success",
                "Sale recorded successfully — stock updated.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/sales";
    }

    // Read-only, so no @PreAuthorize role restriction — any logged-in user
    // (ADMIN or VIEWER) can view sales history, same as /dashboard and /products.
    @GetMapping("/sales/history")
    public String salesHistory(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<com.Luxa.inventory.model.SaleTransaction> salesPage = saleService.getSalesHistory(page, 20);
        model.addAttribute("salesPage", salesPage);
        return "sales-history";
    }
}
