package com.Luxa.inventory.config;

import com.Luxa.inventory.model.Product;
import com.Luxa.inventory.model.Role;
import com.Luxa.inventory.model.User;
import com.Luxa.inventory.repository.ProductRepository;
import com.Luxa.inventory.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    @Profile("dev")
    public CommandLineRunner seedData(
            UserRepository userRepository,
            ProductRepository productRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setRole(Role.ADMIN);
                admin.setEnabled(true);
                userRepository.save(admin);

                User viewer = new User();
                viewer.setUsername("viewer");
                viewer.setPassword(passwordEncoder.encode("viewer"));
                viewer.setRole(Role.VIEWER);
                viewer.setEnabled(true);
                userRepository.save(viewer);

                System.out.println("Seeded users: admin / admin, viewer / viewer");
            }

            if (productRepository.count() > 0) {
                return;
            }

            List<Product> products = List.of(
                    product("Basmati Rice 5kg", "Grains", new BigDecimal("299.00"), 50),
                    product("Whole Milk 1L", "Dairy", new BigDecimal("62.00"), 30),
                    product("Amul Butter 500g", "Dairy", new BigDecimal("245.00"), 4),
                    product("Toor Dal 1kg", "Pulses", new BigDecimal("140.00"), 25),
                    product("Sunflower Oil 1L", "Oils", new BigDecimal("135.00"), 0),
                    product("Atta 10kg", "Grains", new BigDecimal("410.00"), 18),
                    product("Paneer 200g", "Dairy", new BigDecimal("89.00"), 3),
                    product("Green Tea 100g", "Beverages", new BigDecimal("199.00"), 12),
                    product("Maggi Noodles", "Snacks", new BigDecimal("14.00"), 60),
                    product("Besan 500g", "Pulses", new BigDecimal("55.00"), 2),
                    product("Tomato Ketchup", "Condiments", new BigDecimal("110.00"), 8),
                    product("Coconut Oil 500ml", "Oils", new BigDecimal("185.00"), 15),
                    product("Yogurt 400g", "Dairy", new BigDecimal("45.00"), 5),
                    product("Chana Dal 1kg", "Pulses", new BigDecimal("125.00"), 22),
                    product("Biscuits Pack", "Snacks", new BigDecimal("40.00"), 35)
            );

            productRepository.saveAll(products);
            System.out.println("Seeded " + products.size() + " sample products.");
        };
    }

    private Product product(String name, String category, BigDecimal price, int qty) {
        Product p = new Product();
        p.setName(name);
        p.setCategory(category);
        p.setPrice(price);
        p.setQuantity(qty);
        return p;
    }
}
