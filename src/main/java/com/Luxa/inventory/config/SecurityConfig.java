package com.Luxa.inventory.config;

import com.Luxa.inventory.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final UserService userService;

    public SecurityConfig(UserService userService) {
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Truly public routes only: login/register pages, static assets, health check
                .requestMatchers(
                        "/login", "/register",
                        "/css/**", "/js/**",
                        "/actuator/health"
                ).permitAll()
                // Admin-only write actions
                .requestMatchers("/add-product", "/edit-product/**",
                        "/update-product/**", "/delete-product/**").hasRole("ADMIN")
                // Everything else (including /dashboard and /api/**) requires login
                .anyRequest().authenticated()
            )
            .userDetailsService(userService)
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
            )
            .logout(logout -> logout.permitAll());
        return http.build();
    }
}
