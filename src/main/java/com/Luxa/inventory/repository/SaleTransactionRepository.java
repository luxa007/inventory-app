package com.Luxa.inventory.repository;

import com.Luxa.inventory.model.SaleTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface SaleTransactionRepository extends JpaRepository<SaleTransaction, Long> {

    List<SaleTransaction> findByProductIdOrderByTimestampDesc(Long productId);

    @Query("SELECT SUM(s.quantitySold) FROM SaleTransaction s WHERE s.product.id = :productId AND s.timestamp >= :since")
    Integer sumQuantitySoldByProductSince(Long productId, LocalDateTime since);

    @Query("SELECT SUM(s.totalAmount) FROM SaleTransaction s WHERE s.timestamp >= :since")
    java.math.BigDecimal sumRevenueSince(LocalDateTime since);

    @Query("SELECT s.product.id, SUM(s.quantitySold) FROM SaleTransaction s GROUP BY s.product.id ORDER BY SUM(s.quantitySold) DESC")
    List<Object[]> findTopSellingProducts();
}
