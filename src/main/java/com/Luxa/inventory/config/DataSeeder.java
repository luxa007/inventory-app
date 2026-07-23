package com.Luxa.inventory.config;

import com.Luxa.inventory.model.Product;
import com.Luxa.inventory.model.Role;
import com.Luxa.inventory.model.User;
import com.Luxa.inventory.repository.ProductRepository;
import com.Luxa.inventory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;

@Configuration
public class DataSeeder {

    @Value("${app.seed.admin-password:}")
    private String adminPasswordOverride;

    @Value("${app.seed.viewer-password:}")
    private String viewerPasswordOverride;

    @Bean
    @Profile({"dev", "prod"})
    public CommandLineRunner seedData(
            ProductRepository productRepo,
            UserRepository userRepo,
            PasswordEncoder passwordEncoder) {
        return args -> {
            seedUserIfMissing(userRepo, passwordEncoder, "admin", adminPasswordOverride, Role.ADMIN);
            seedUserIfMissing(userRepo, passwordEncoder, "viewer", viewerPasswordOverride, Role.VIEWER);

            if (productRepo.count() > 0) return;
            List<Product> products = List.of(
                product("Basmati Rice 5kg",  "Grains",     new BigDecimal("299.00"), 50),
                product("Whole Milk 1L",     "Dairy",      new BigDecimal("62.00"),  30),
                product("Amul Butter 500g",  "Dairy",      new BigDecimal("245.00"), 4),
                product("Toor Dal 1kg",      "Pulses",     new BigDecimal("140.00"), 25),
                product("Sunflower Oil 1L",  "Oils",       new BigDecimal("135.00"), 0),
                product("Atta 10kg",         "Grains",     new BigDecimal("410.00"), 18),
                product("Paneer 200g",       "Dairy",      new BigDecimal("89.00"),  3),
                product("Green Tea 100g",    "Beverages",  new BigDecimal("199.00"), 12),
                product("Maggi Noodles",     "Snacks",     new BigDecimal("14.00"),  60),
                product("Besan 500g",        "Pulses",     new BigDecimal("55.00"),  2),
                product("Tomato Ketchup",    "Condiments", new BigDecimal("110.00"), 8),
                product("Coconut Oil 500ml", "Oils",       new BigDecimal("185.00"), 15),
                product("Yogurt 400g",       "Dairy",      new BigDecimal("45.00"),  5),
                product("Chana Dal 1kg",     "Pulses",     new BigDecimal("125.00"), 22),
                product("Biscuits Pack",     "Snacks",     new BigDecimal("40.00"),  35)
            );
            productRepo.saveAll(products);
            System.out.println("Seeded " + products.size() + " sample products.");
        };
    }

    /**
     * Only creates the user if it does not already exist.
     * Never overwrites an existing user's password on restart/redeploy.
     * If no password override is supplied via env var, generates a random
     * one-time password and prints it once so it can be captured and stored securely.
     */
    private void seedUserIfMissing(UserRepository userRepo, PasswordEncoder encoder,
                                    String username, String passwordOverride, Role role) {
        if (userRepo.findByUsername(username).isPresent()) {
            return;
        }
        String rawPassword = (passwordOverride != null && !passwordOverride.isBlank())
                ? passwordOverride
                : generateRandomPassword();

        User user = new User();
        user.setUsername(username);
        user.setPassword(encoder.encode(rawPassword));
        user.setRole(role);
        user.setEnabled(true);
        userRepo.save(user);

        if (passwordOverride == null || passwordOverride.isBlank()) {
            System.out.println("Seeded new user '" + username
                    + "' with generated password: " + rawPassword
                    + "  (SAVE THIS NOW - it will not be shown again. Log in and change it immediately.)");
        } else {
            System.out.println("Seeded new user '" + username + "' from configured password.");
        }
    }

    private String generateRandomPassword() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789!@#$%";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(16);
        for (int i = 0; i < 16; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
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
