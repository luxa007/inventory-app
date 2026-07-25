package com.Luxa.inventory;

import com.Luxa.inventory.exception.ResourceNotFoundException;
import com.Luxa.inventory.model.Product;
import com.Luxa.inventory.repository.ProductRepository;
import com.Luxa.inventory.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    ProductService productService;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        sampleProduct = new Product();
        sampleProduct.setId(1L);
        sampleProduct.setName("Basmati Rice");
        sampleProduct.setCategory("Grains");
        sampleProduct.setQuantity(3);
        sampleProduct.setMinThreshold(5);
        sampleProduct.setPrice(new BigDecimal("450.00"));
    }

    @Test
    void isLowStock_whenQuantityBelowThreshold_returnsTrue() {
        assertThat(sampleProduct.isLowStock()).isTrue();
    }

    @Test
    void isLowStock_whenQuantityAboveThreshold_returnsFalse() {
        sampleProduct.setQuantity(10);
        assertThat(sampleProduct.isLowStock()).isFalse();
    }

    @Test
    void isLowStock_whenQuantityEqualsThreshold_returnsTrue() {
        sampleProduct.setQuantity(5);
        assertThat(sampleProduct.isLowStock()).isTrue();
    }

    @Test
    void requireById_whenProductExists_returnsProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        Product result = productService.requireById(1L);
        assertThat(result.getName()).isEqualTo("Basmati Rice");
    }

    @Test
    void requireById_whenProductMissing_throwsResourceNotFoundException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> productService.requireById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void update_changesFieldsAndSaves() {
        Product incoming = new Product();
        incoming.setName("Updated Rice");
        incoming.setCategory("Grains");
        incoming.setQuantity(20);
        incoming.setPrice(new BigDecimal("500.00"));

        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Product result = productService.update(1L, incoming);

        assertThat(result.getName()).isEqualTo("Updated Rice");
        assertThat(result.getQuantity()).isEqualTo(20);
        verify(productRepository).save(sampleProduct);
    }

    @Test
    void countLowStock_returnsCorrectCount() {
        when(productRepository.countLowStockByOwnThreshold()).thenReturn(1L);
        assertThat(productService.countLowStock()).isEqualTo(1L);
    }

    @Test
    void getTotalInventoryValue_whenNull_returnsZero() {
        when(productRepository.sumInventoryValue()).thenReturn(null);
        assertThat(productService.getTotalInventoryValue()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void deleteById_callsRepository() {
        productService.deleteById(1L);
        verify(productRepository).deleteById(1L);
    }
}
