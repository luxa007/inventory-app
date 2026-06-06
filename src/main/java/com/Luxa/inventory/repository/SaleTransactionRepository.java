package com.Luxa.inventory.repository;

import com.Luxa.inventory.model.SaleTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface SaleTransactionRepository extends JpaRepository<SaleTransaction, Long> {

    List<SaleTransaction> findByProductIdOrderByTimestampDesc(Long productId);

    List<SaleTransaction> findTop20ByOrderByTimestampDesc();

    @Query("SELECT SUM(s.quantitySold) FROM SaleTransaction s WHERE s.product.id = :productId AND s.timestamp >= :since")
    Integer sumQuantitySoldByProductSince(@Param("productId") Long productId, @Param("since") LocalDateTime since);

    @Query("SELECT SUM(s.totalAmount) FROM SaleTransaction s WHERE s.timestamp >= :since")
    BigDecimal sumRevenueSince(@Param("since") LocalDateTime since);

    @Query("SELECT s.product.id, SUM(s.quantitySold) FROM SaleTransaction s GROUP BY s.product.id ORDER BY SUM(s.quantitySold) DESC")
    List<Object[]> findTopSellingProducts();
}
