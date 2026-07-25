package com.Luxa.inventory.service;

import com.Luxa.inventory.exception.ResourceNotFoundException;
import com.Luxa.inventory.model.Product;
import com.Luxa.inventory.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private static final int PAGE_SIZE = 10;
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() { return productRepository.findAll(); }
    public List<Product> findAll() { return productRepository.findAll(); }

    public Page<Product> search(String keyword, int page) {
        String kw = (keyword == null) ? "" : keyword.trim();
        PageRequest pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("id").descending());
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

    // Per-product threshold ("units" varies by product, so there's no single
    // number to display here). Kept for template compatibility but templates
    // should prefer showing each product's own minThreshold instead.
    public int getLowStockThreshold() { return 5; }

    public long countLowStock() {
        return productRepository.countLowStockByOwnThreshold();
    }

    public List<Product> getLowStockProducts() {
        return productRepository.findAll().stream()
                .filter(Product::isLowStock)
                .collect(Collectors.toList());
    }

    public List<Product> findLowStockProducts() {
        return productRepository.findLowStockByOwnThreshold();
    }

    public long countAll() { return productRepository.count(); }

    public java.math.BigDecimal getTotalInventoryValue() {
        java.math.BigDecimal v = productRepository.sumInventoryValue();
        return v != null ? v : java.math.BigDecimal.ZERO;
    }

    public Product requireById(long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found (id=" + id + ")."));
    }

    @Transactional
    public Product save(Product product) { return productRepository.save(product); }

    @Transactional
    public Product update(long id, Product incoming) {
        Product existing = requireById(id);
        existing.setName(incoming.getName());
        existing.setCategory(incoming.getCategory());
        existing.setPrice(incoming.getPrice());
        existing.setQuantity(incoming.getQuantity());
        existing.setMinThreshold(incoming.getMinThreshold());
        return productRepository.save(existing);
    }

    @Transactional
    public void deleteById(long id) { productRepository.deleteById(id); }
}
