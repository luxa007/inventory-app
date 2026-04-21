package com.Luxa.inventory.service;

import com.Luxa.inventory.exception.ResourceNotFoundException;
import com.Luxa.inventory.model.Product;
import com.Luxa.inventory.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private static final int LOW_STOCK_THRESHOLD = 5;
    private static final int PAGE_SIZE = 10;
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() { return productRepository.findAll(); }
    public List<Product> findAll() { return productRepository.findAll(); }

    public Page<Product> search(String keyword, int page) {
        String kw = (keyword == null) ? "" : keyword.trim();
        PageRequest pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("name").ascending());
        return productRepository.search(kw, pageable);
    }

    public List<String> findAllCategories() {
        return productRepository.findAll().stream()
                .map(Product::getCategory)
                .filter(c -> c != null && !c.isBlank())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public int getLowStockThreshold() { return LOW_STOCK_THRESHOLD; }
    
    public long countLowStock() { 
        return productRepository.findLowStockExclusive(LOW_STOCK_THRESHOLD).size(); 
    }

    public List<Product> getLowStockProducts() {
        return productRepository.findAll().stream()
                .filter(Product::isLowStock)
                .collect(Collectors.toList());
    }

    public List<Product> findLowStockProducts() {
        return productRepository.findLowStockExclusive(LOW_STOCK_THRESHOLD);
    }

    public Product save(Product product) { return productRepository.save(product); }

    public Product requireById(long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found (id=" + id + ")."));
    }

    public Product update(long id, Product incoming) {
        Product existing = requireById(id);
        existing.setName(incoming.getName());
        existing.setCategory(incoming.getCategory());
        existing.setPrice(incoming.getPrice());
        existing.setQuantity(incoming.getQuantity());
        return productRepository.save(existing);
    }

    public void deleteById(long id) { productRepository.deleteById(id); }
}
