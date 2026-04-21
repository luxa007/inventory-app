package com.Luxa.inventory.controller;

import com.Luxa.inventory.model.Product;
import com.Luxa.inventory.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public String listProducts(@RequestParam(defaultValue = "") String keyword,
                               @RequestParam(defaultValue = "0") int page,
                               Model model) {
        Page<Product> productPage = productService.search(keyword, page);
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("page", productPage);
        model.addAttribute("categories", productService.findAllCategories());
        model.addAttribute("lowStockCount", productService.countLowStock());
        model.addAttribute("lowStockThreshold", productService.getLowStockThreshold());
        model.addAttribute("keyword", keyword);
        return "products";
    }

    @GetMapping("/products/low-stock")
    public String lowStock(Model model) {
        model.addAttribute("products", productService.findLowStockProducts());
        model.addAttribute("lowStockThreshold", productService.getLowStockThreshold());
        return "low-stock";
    }

    @GetMapping("/add-product")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", productService.findAllCategories());
        return "add-product";
    }

    @PostMapping("/add-product")
    public String saveProduct(@ModelAttribute Product product, RedirectAttributes ra) {
        productService.save(product);
        ra.addFlashAttribute("successMessage", "Product added successfully.");
        return "redirect:/products";
    }

    @GetMapping("/edit-product/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.requireById(id));
        model.addAttribute("categories", productService.findAllCategories());
        return "edit-product";
    }

    @PostMapping("/update-product/{id}")
    public String updateProduct(@PathVariable Long id,
                                @ModelAttribute Product product,
                                RedirectAttributes ra) {
        productService.update(id, product);
        ra.addFlashAttribute("successMessage", "Product updated successfully.");
        return "redirect:/products";
    }

    @PostMapping("/delete-product/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes ra) {
        productService.requireById(id);
        productService.deleteById(id);
        ra.addFlashAttribute("successMessage", "Product deleted successfully.");
        return "redirect:/products";
    }
}
