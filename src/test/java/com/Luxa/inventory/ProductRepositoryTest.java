package com.Luxa.inventory;

import com.Luxa.inventory.model.Product;
import com.Luxa.inventory.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
class ProductRepositoryTest {

    @Autowired
    ProductRepository productRepository;

    private Product buildProduct(String name, String category, int qty, BigDecimal price) {
        Product p = new Product();
        p.setName(name);
        p.setCategory(category);
        p.setQuantity(qty);
        p.setPrice(price);
        return p;
    }

    @Test
    void search_findsByNameCaseInsensitive() {
        productRepository.save(buildProduct("Basmati Rice", "Grains", 5, new BigDecimal("10.00")));

        Page<Product> page = productRepository.search("basmati", PageRequest.of(0, 10));
        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getName()).isEqualTo("Basmati Rice");
    }

    @Test
    void findLowStockExclusive_excludesZero() {
        productRepository.save(buildProduct("Zero Stock", "Grocery", 0, BigDecimal.ONE));
        productRepository.save(buildProduct("Low Stock", "Grocery", 3, BigDecimal.ONE));

        assertThat(productRepository.findLowStockExclusive(5))
                .extracting(Product::getName)
                .containsExactly("Low Stock");
    }

    @Test
    void sumInventoryValue_multipliesPriceAndQty() {
        productRepository.save(buildProduct("Test Product", "Category", 3, new BigDecimal("10.00")));

        assertThat(productRepository.sumInventoryValue())
                .isEqualByComparingTo(new BigDecimal("30.00"));
    }
}
