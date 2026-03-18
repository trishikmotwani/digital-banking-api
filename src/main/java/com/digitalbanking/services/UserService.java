package com.digitalbanking.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.digitalbanking.dtos.UserDto;
import com.digitalbanking.entities.CustomerEntity;
import com.digitalbanking.entities.UserEntity;
import com.digitalbanking.entities.UserRole;
import com.digitalbanking.exceptions.UserAlreadyExistsException;
import com.digitalbanking.exceptions.UserNotFoundException;
import com.digitalbanking.repositories.UserRepository;
import com.digitalbanking.security.jwt.JWTService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
public class UserService implements IUserService {

	@Autowired
    private UserRepository userRepository;
	
	@Autowired
    private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private JWTService jwtService;
	
    @Override
    public String login(String username, String password) {
        // 1. Find user in DB
    	UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));

        // 2. Check if password matches (encoded)
    	if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password"); // You could create an InvalidCredentialsException for this too!
        }
    	
        // 3. Update last logged in
        user.setLastLoggedIn(LocalDateTime.now());
        userRepository.save(user);

        // 4. Return token (usually a JWT - simplified here)
        String jwtToken = jwtService.generateToken(username);
        return jwtToken;
    }
    
    @Override
    @Transactional
    public UserDto registerUser(UserEntity user) {
        // 1. Check if username exists
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UserAlreadyExistsException("Username '" + user.getUsername() + "' is already taken.");
        }

        // 2. Encode the password
        String encodedPassword = passwordEncoder.encode(user.getPassword());

        UserEntity savedUser;

        // 3. Handle Role-Based Entity Creation
        if (user.getRole() == UserRole.ROLE_USER) {
            // Create a CustomerEntity to ensure records are created in BOTH tables
            CustomerEntity customer = new CustomerEntity();
            BeanUtils.copyProperties(user, customer); // Copies username, etc.
            customer.setPassword(encodedPassword);
            
            // Set mandatory customer-specific defaults
            customer.setKycVerified(false);
            customer.setDeleted(false);
            customer.setIncome(0.0);
            customer.setAge(18); // Default or extract from request if available

            savedUser = userRepository.save(customer);
        } else {
            // Standard UserEntity for ADMIN/MANAGER
            user.setPassword(encodedPassword);
            savedUser = userRepository.save(user);
        }

        // 4. Map to DTO
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(savedUser, userDto);
        return userDto;
    }

    @Override
    public void logout(String username) {
        // In Stateless JWT, logout is usually handled on the frontend (delete token)
        // Or you can blacklist the token in Redis here
        System.out.println("User " + username + " logged out.");
    }

    @Override
    public String resetPassword(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Email not found"));
        
        // Logic to generate a unique UUID token and send an email
        String resetToken = UUID.randomUUID().toString();
        return "https://bank.com" + resetToken;
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<UserEntity> users = userRepository.findAll();

        return users.stream().map(userEntity -> {
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(userEntity, userDto);
            return userDto;
        }).collect(Collectors.toList());
    }

    @Override
    public UserDto getUserByUsername(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        // Map Entity to DTO (Assuming you have a mapper or a constructor)
        UserDto dto = new UserDto();
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());
        // Add other fields like lastLoggedIn if needed
        return dto;
    }
    
    @Override
    @Transactional
    public void updateUserRole(String userId, UserRole newRole) {
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setRole(newRole);
        userRepository.save(user);
    }

}

