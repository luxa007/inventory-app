package com.Luxa.inventory.service;

import com.Luxa.inventory.model.*;
import com.Luxa.inventory.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SaleService {

    private final SaleTransactionRepository saleRepo;
    private final StockMovementRepository movementRepo;
    private final ProductRepository productRepo;

    public SaleService(SaleTransactionRepository saleRepo,
                       StockMovementRepository movementRepo,
                       ProductRepository productRepo) {
        this.saleRepo = saleRepo;
        this.movementRepo = movementRepo;
        this.productRepo = productRepo;
    }

    @Transactional
    public SaleTransaction recordSale(Long productId, int quantity, String soldBy) {
        Product product = productRepo.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        if (product.getQuantity() < quantity) {
            throw new IllegalStateException(
                "Insufficient stock. Available: " + product.getQuantity() + ", Requested: " + quantity);
        }

        // Decrement stock
        product.setQuantity(product.getQuantity() - quantity);

        // Update sales velocity (units/day over last 30 days)
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        Integer unitsSold = saleRepo.sumQuantitySoldByProductSince(productId, thirtyDaysAgo);
        double velocity = (unitsSold != null ? unitsSold + quantity : quantity) / 30.0;
        product.setSalesVelocity(velocity);
        productRepo.save(product);

        // Record sale transaction
        SaleTransaction sale = new SaleTransaction();
        sale.setProduct(product);
        sale.setQuantitySold(quantity);
        sale.setUnitPriceAtSale(product.getPrice());
        sale.setTotalAmount(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        sale.setSoldBy(soldBy);
        saleRepo.save(sale);

        // Audit trail
        StockMovement movement = new StockMovement();
        movement.setProduct(product);
        movement.setQuantityDelta(-quantity);
        movement.setType(StockMovement.MovementType.SALE);
        movement.setNote("Sale recorded");
        movement.setPerformedBy(soldBy);
        movementRepo.save(movement);

        return sale;
    }

    @Transactional
    public void receiveStock(Long productId, int quantity, String receivedBy, String note) {
        Product product = productRepo.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        product.setQuantity(product.getQuantity() + quantity);
        productRepo.save(product);

        StockMovement movement = new StockMovement();
        movement.setProduct(product);
        movement.setQuantityDelta(quantity);
        movement.setType(StockMovement.MovementType.PURCHASE);
        movement.setNote(note != null ? note : "Stock received");
        movement.setPerformedBy(receivedBy);
        movementRepo.save(movement);
    }

    public List<SaleTransaction> getSalesForProduct(Long productId) {
        return saleRepo.findByProductIdOrderByTimestampDesc(productId);
    }

    public List<StockMovement> getMovementsForProduct(Long productId) {
        return movementRepo.findByProductIdOrderByTimestampDesc(productId);
    }

    public List<StockMovement> getRecentMovements() {
        return movementRepo.findTop20ByOrderByTimestampDesc();
    }

    public BigDecimal getTodayRevenue() {
        BigDecimal revenue = saleRepo.sumRevenueSince(LocalDateTime.now().withHour(0).withMinute(0));
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    public java.util.List<com.Luxa.inventory.model.SaleTransaction> getRecentSales() {
        return saleRepo.findTop20ByOrderByTimestampDesc();
    }

    public Page<SaleTransaction> getSalesHistory(int page, int size) {
        return saleRepo.findAllByOrderByTimestampDesc(PageRequest.of(page, size));
    }

    public BigDecimal getMonthlyRevenue() {
        BigDecimal revenue = saleRepo.sumRevenueSince(LocalDateTime.now().minusDays(30));
        return revenue != null ? revenue : BigDecimal.ZERO;
    }
}
// Appending getRecentSales - ignore this comment
