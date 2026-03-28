package com.Luxa.inventory.service;

import com.Luxa.inventory.exception.ResourceNotFoundException;
import com.Luxa.inventory.model.Product;
import com.Luxa.inventory.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final int lowStockThreshold;
    private static final int DEFAULT_PAGE_SIZE = 10;

    private final ProductRepository productRepository;

    public ProductService(
            ProductRepository productRepository,
            @Value("${app.low-stock-threshold:5}") int lowStockThreshold) {
        this.productRepository = productRepository;
        this.lowStockThreshold = lowStockThreshold;
    }

    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return productRepository.findAll(Sort.by("name"));
    }

    @Transactional(readOnly = true)
    public Page<Product> findAllPaged(int page) {
        Pageable pageable = PageRequest.of(clampPage(page), DEFAULT_PAGE_SIZE, Sort.by("name"));
        return productRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Product requireById(Long id) {
        return findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found (id=" + id + ")."));
    }

    @Transactional
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public Product update(Long id, Product incoming) {
        Product existing = requireById(id);
        existing.setName(incoming.getName());
        existing.setCategory(incoming.getCategory());
        existing.setPrice(incoming.getPrice());
        existing.setQuantity(incoming.getQuantity());
        return productRepository.save(existing);
    }

    @Transactional
    public void deleteById(Long id) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found (id=" + id + ")."));
        productRepository.delete(existing);
    }

    @Transactional(readOnly = true)
    public Page<Product> search(String query, int page) {
        Pageable pageable = PageRequest.of(clampPage(page), DEFAULT_PAGE_SIZE, Sort.by("name"));
        if (query == null || query.isBlank()) {
            return productRepository.findAll(pageable);
        }
        return productRepository.search(query.trim(), pageable);
    }

    @Transactional(readOnly = true)
    public Page<Product> findByCategory(String category, int page) {
        Pageable pageable = PageRequest.of(clampPage(page), DEFAULT_PAGE_SIZE, Sort.by("name"));
        return productRepository.findByCategory(category, pageable);
    }

    @Transactional(readOnly = true)
    public List<String> findAllCategories() {
        return productRepository.findAllCategories();
    }

    /** Quantity in (0, threshold] — excludes zero (see {@link #findOutOfStockProducts()}). */
    @Transactional(readOnly = true)
    public List<Product> findLowStockProducts() {
        return productRepository.findLowStockExclusive(lowStockThreshold);
    }

    @Transactional(readOnly = true)
    public List<Product> findOutOfStockProducts() {
        return productRepository.findOutOfStockOrdered();
    }

    @Transactional(readOnly = true)
    public long countAll() {
        return productRepository.count();
    }

    @Transactional(readOnly = true)
    public long countLowStock() {
        return productRepository.countByQuantityLessThanEqual(lowStockThreshold);
    }

    @Transactional(readOnly = true)
    public int getLowStockThreshold() {
        return lowStockThreshold;
    }

    private static int clampPage(int page) {
        return Math.max(0, page);
    }
}
