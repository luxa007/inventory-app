package com.Luxa.inventory;

import com.Luxa.inventory.config.SecurityConfig;
import com.Luxa.inventory.controller.ProductController;
import com.Luxa.inventory.exception.GlobalExceptionHandler;
import com.Luxa.inventory.exception.ResourceNotFoundException;
import com.Luxa.inventory.model.Product;
import com.Luxa.inventory.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = ProductController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
@MockBean(UserDetailsService.class)
class ProductControllerWebTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ProductService productService;

    @Test
    @WithMockUser(roles = "VIEWER")
    void productsPageLoads() throws Exception {
        when(productService.search(anyString(), anyInt()))
                .thenReturn(new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0));
        when(productService.findAllCategories()).thenReturn(List.of());
        when(productService.countLowStock()).thenReturn(0L);
        when(productService.getLowStockThreshold()).thenReturn(5);

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("products"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("lowStockThreshold", 5));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addProduct_withValidFields_redirects() throws Exception {
        when(productService.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(post("/add-product")
                        .with(csrf())
                        .param("name", "Rice")
                        .param("price", "10.50")
                        .param("quantity", "5")
                        .param("category", "Grocery"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/products"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(productService).save(any(Product.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void editProduct_notFound_returns404() throws Exception {
        when(productService.requireById(99L))
                .thenThrow(new ResourceNotFoundException("Product not found (id=99)."));

        mockMvc.perform(get("/edit-product/99"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateProduct_postsToPathId() throws Exception {
        when(productService.update(eq(1L), any(Product.class))).thenAnswer(inv -> inv.getArgument(1));

        mockMvc.perform(post("/update-product/1")
                        .with(csrf())
                        .param("id", "1")
                        .param("name", "Milk")
                        .param("price", "2.50")
                        .param("quantity", "4")
                        .param("category", "Dairy"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/products"));

        verify(productService).update(eq(1L), any(Product.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteProduct_redirects() throws Exception {
        Product p = new Product();
        p.setId(9L);
        p.setName("X");
        p.setCategory("C");
        p.setPrice(BigDecimal.ONE);
        p.setQuantity(1);
        when(productService.requireById(9L)).thenReturn(p);

        mockMvc.perform(post("/delete-product/9").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/products"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(productService).deleteById(eq(9L));
    }

    @Test
    @WithMockUser(roles = "VIEWER")
    void lowStockPageLoads() throws Exception {
        when(productService.findLowStockProducts()).thenReturn(List.of());
        when(productService.findOutOfStockProducts()).thenReturn(List.of());
        when(productService.getLowStockThreshold()).thenReturn(5);

        mockMvc.perform(get("/low-stock"))
                .andExpect(status().isOk())
                .andExpect(view().name("low-stock"));
    }
}
