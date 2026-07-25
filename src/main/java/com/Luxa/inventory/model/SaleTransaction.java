package com.Luxa.inventory.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sale_transaction")
public class SaleTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantitySold;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPriceAtSale;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private String soldBy;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    public Long getId() { return id; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public Integer getQuantitySold() { return quantitySold; }
    public void setQuantitySold(Integer quantitySold) { this.quantitySold = quantitySold; }
    public BigDecimal getUnitPriceAtSale() { return unitPriceAtSale; }
    public void setUnitPriceAtSale(BigDecimal unitPriceAtSale) { this.unitPriceAtSale = unitPriceAtSale; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getSoldBy() { return soldBy; }
    public void setSoldBy(String soldBy) { this.soldBy = soldBy; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}