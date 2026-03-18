package com.digitalbanking.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digitalbanking.dtos.UserDto;
import com.digitalbanking.entities.UserEntity;
import com.digitalbanking.entities.UserRole;
import com.digitalbanking.services.IUserService;

@RestController
@RequestMapping("/api/user") // Plural is standard REST convention
public class UserController {

	@Autowired
    private IUserService userService;

    @PostMapping("/register-user")
    public ResponseEntity<UserDto> register(@RequestBody UserEntity user) {
        UserDto userDto = userService.registerUser(user);
        // Returns 201 Created instead of 200 OK for new resources
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserEntity user) {
        String token = userService.login(user.getUsername(), user.getPassword());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/reset-password/{username}") // Use POST for actions that change state
    public ResponseEntity<String> resetPassword(@PathVariable String username) {
        userService.resetPassword(username);
        return ResponseEntity.ok("Reset link has been sent to registered email address.");
    }
    
    @GetMapping("/get-all") // Use POST for actions that change state
    public ResponseEntity<?> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.CREATED);
    }
    
    @GetMapping("/details/{username}")
    public ResponseEntity<UserDto> getUserDetails(@PathVariable("username") String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }
    
    @PatchMapping("/{userId}/role")
	 // You should protect this with @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
	 public ResponseEntity<String> updateRole(@PathVariable("userId") String userId, @RequestBody UserRole role) {
	     userService.updateUserRole(userId, role);
	     return ResponseEntity.ok("User role updated to " + role);
	 }
}
