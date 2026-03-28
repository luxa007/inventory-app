package com.Luxa.inventory;

import com.Luxa.inventory.exception.ResourceNotFoundException;
import com.Luxa.inventory.model.Product;
import com.Luxa.inventory.repository.ProductRepository;
import com.Luxa.inventory.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;

    ProductService productService;

    @BeforeEach
    void init() {
        productService = new ProductService(productRepository, 5);
    }

    @Test
    void update_usesPathIdAndIgnoresTamperedPayloadId() {
        Product existing = new Product();
        existing.setId(1L);
        existing.setName("Old");
        existing.setCategory("C");
        existing.setPrice(BigDecimal.TEN);
        existing.setQuantity(2);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Product incoming = new Product();
        incoming.setId(999L);
        incoming.setName("New");
        incoming.setCategory("D");
        incoming.setPrice(new BigDecimal("3.50"));
        incoming.setQuantity(7);

        Product result = productService.update(1L, incoming);

        assertThat(result.getName()).isEqualTo("New");
        assertThat(result.getId()).isEqualTo(1L);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void update_missingProduct_throwsNotFound() {
        when(productRepository.findById(42L)).thenReturn(Optional.empty());

        Product incoming = new Product();
        incoming.setName("X");
        incoming.setCategory("Y");
        incoming.setPrice(BigDecimal.ONE);
        incoming.setQuantity(1);

        assertThatThrownBy(() -> productService.update(42L, incoming))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(productRepository, never()).save(any());
    }
}
