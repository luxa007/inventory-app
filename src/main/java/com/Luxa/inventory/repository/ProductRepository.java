package com.Luxa.inventory.repository;

import com.Luxa.inventory.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    long countByQuantityBetween(int start, int end);
    long countByQuantity(int quantity);

    @Query("SELECT SUM(p.price * p.quantity) FROM Product p")
    BigDecimal sumInventoryValue();

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :kw, '%')) " +
           "OR LOWER(p.category) LIKE LOWER(CONCAT('%', :kw, '%'))")
    Page<Product> search(@Param("kw") String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.quantity > 0 AND p.quantity < :threshold")
    List<Product> findLowStockExclusive(@Param("threshold") int threshold);
}
