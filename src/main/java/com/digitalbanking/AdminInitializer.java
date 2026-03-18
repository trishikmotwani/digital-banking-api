package com.digitalbanking;

import com.digitalbanking.entities.UserEntity;
import com.digitalbanking.entities.UserRole;
import com.digitalbanking.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AdminInitializer(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Only create if no admin exists to avoid duplicates
        if (userRepository.findByUsername("admin").isEmpty()) {
            UserEntity admin = new UserEntity();
            admin.setUsername("admin");
            // Encodes "admin123" correctly using your BCrypt bean
            admin.setPassword(passwordEncoder.encode("admin123")); 
            admin.setRole(UserRole.ROLE_ADMIN);
            admin.setCreatedAt(LocalDateTime.now());
            admin.setUpdatedAt(LocalDateTime.now());

            userRepository.save(admin);
            System.out.println("✅ Default Admin User created: admin / admin123");
        } else {
            System.out.println("ℹ️ Admin user already exists. Skipping initialization.");
        }
    }
}
