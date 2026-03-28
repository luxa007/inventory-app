package com.Luxa.inventory.controller;

import com.Luxa.inventory.exception.ResourceNotFoundException;
import com.Luxa.inventory.model.Product;
import com.Luxa.inventory.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public String listProducts(
            @RequestParam(defaultValue = "") String query,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        Page<Product> productPage = productService.search(query, page);

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", productPage.getNumber());
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());
        model.addAttribute("query", query);
        model.addAttribute("categories", productService.findAllCategories());
        model.addAttribute("lowStockCount", productService.countLowStock());
        model.addAttribute("lowStockThreshold", productService.getLowStockThreshold());
        model.addAttribute("pageTitle", "Products");
        return "products";
    }

    @GetMapping("/add-product")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", productService.findAllCategories());
        model.addAttribute("formAction", "/add-product");
        model.addAttribute("pageTitle", "Add Product");
        return "product-form";
    }

    @PostMapping("/add-product")
    public String saveProduct(
            @Valid @ModelAttribute Product product,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", productService.findAllCategories());
            model.addAttribute("formAction", "/add-product");
            model.addAttribute("pageTitle", "Add Product");
            return "product-form";
        }

        productService.save(product);
        redirectAttributes.addFlashAttribute("successMessage",
                "Product \"" + product.getName() + "\" added successfully.");
        return "redirect:/products";
    }

    @GetMapping("/edit-product/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.requireById(id);
        model.addAttribute("product", product);
        model.addAttribute("categories", productService.findAllCategories());
        model.addAttribute("formAction", "/update-product/" + id);
        model.addAttribute("pageTitle", "Edit Product");
        return "product-form";
    }

    @PostMapping("/update-product/{id}")
    public String updateProduct(
            @PathVariable Long id,
            @Valid @ModelAttribute Product product,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            product.setId(id);
            model.addAttribute("categories", productService.findAllCategories());
            model.addAttribute("formAction", "/update-product/" + id);
            model.addAttribute("pageTitle", "Edit Product");
            return "product-form";
        }

        if (product.getId() != null && !product.getId().equals(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid product update request.");
            return "redirect:/products";
        }

        productService.update(id, product);
        redirectAttributes.addFlashAttribute("successMessage",
                "Product \"" + product.getName() + "\" updated successfully.");
        return "redirect:/products";
    }

    @PostMapping("/delete-product/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.requireById(id);
            String name = product.getName();
            productService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Product \"" + name + "\" deleted successfully.");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/products";
    }

    @GetMapping("/low-stock")
    public String lowStock(Model model) {
        model.addAttribute("lowStockProducts", productService.findLowStockProducts());
        model.addAttribute("outOfStockProducts", productService.findOutOfStockProducts());
        model.addAttribute("threshold", productService.getLowStockThreshold());
        model.addAttribute("pageTitle", "Stock alerts");
        return "low-stock";
    }
}
