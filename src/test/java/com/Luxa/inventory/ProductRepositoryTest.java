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

    @Test
    void search_findsByNameCaseInsensitive() {
        Product p = new Product();
        p.setName("Basmati Rice");
        p.setCategory("Grains");
        p.setPrice(new BigDecimal("10.00"));
        p.setQuantity(5);
        productRepository.save(p);

        Page<Product> page = productRepository.search("basmati", PageRequest.of(0, 10));
        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getName()).isEqualTo("Basmati Rice");
    }

    @Test
    void findLowStockExclusive_excludesZero() {
        Product a = new Product();
        a.setName("Zero");
        a.setCategory("X");
        a.setPrice(BigDecimal.ONE);
        a.setQuantity(0);
        productRepository.save(a);

        Product b = new Product();
        b.setName("Low");
        b.setCategory("X");
        b.setPrice(BigDecimal.ONE);
        b.setQuantity(3);
        productRepository.save(b);

        assertThat(productRepository.findLowStockExclusive(5))
                .extracting(Product::getName)
                .containsExactly("Low");
    }

    @Test
    void sumInventoryValue_multipliesPriceAndQty() {
        Product p = new Product();
        p.setName("A");
        p.setCategory("C");
        p.setPrice(new BigDecimal("10.00"));
        p.setQuantity(3);
        productRepository.save(p);

        assertThat(productRepository.sumInventoryValue()).isEqualByComparingTo(new BigDecimal("30.00"));
    }
}
