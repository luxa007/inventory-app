package com.Luxa.inventory.service;

import com.Luxa.inventory.model.Role;
import com.Luxa.inventory.model.User;
import com.Luxa.inventory.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(String username, String rawPassword) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required.");
        }
        if (rawPassword == null || rawPassword.length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters.");
        }
        if (userRepository.existsByUsername(username.trim())) {
            throw new IllegalArgumentException("Username already taken.");
        }
        User u = new User();
        u.setUsername(username.trim());
        u.setPassword(passwordEncoder.encode(rawPassword));
        u.setRole(Role.VIEWER);
        u.setEnabled(true);
        userRepository.save(u);
    }
}
