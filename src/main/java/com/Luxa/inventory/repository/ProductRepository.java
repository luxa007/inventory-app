package com.Luxa.inventory.repository;

import com.Luxa.inventory.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.category) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Product> search(@Param("query") String query, Pageable pageable);

    Page<Product> findByCategory(String category, Pageable pageable);

    /** Low stock: quantity in (1 .. threshold] — excludes out-of-stock (0). */
    @Query("SELECT p FROM Product p WHERE p.quantity > 0 AND p.quantity <= :threshold ORDER BY p.quantity ASC, p.name ASC")
    List<Product> findLowStockExclusive(@Param("threshold") int threshold);

    @Query("SELECT p FROM Product p WHERE p.quantity = 0 ORDER BY p.name ASC")
    List<Product> findOutOfStockOrdered();

    List<Product> findByQuantity(Integer quantity);

    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.category IS NOT NULL ORDER BY p.category")
    List<String> findAllCategories();

    long countByCategory(String category);

    long countByQuantityLessThanEqual(int quantity);

    long countByQuantity(int quantity);

    long countByQuantityBetween(int minInclusive, int maxInclusive);

    @Query("SELECT COALESCE(SUM(p.price * p.quantity), 0) FROM Product p")
    BigDecimal sumInventoryValue();
}
