package com.booking.thirdeye.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.booking.thirdeye.entity.User;
import com.booking.thirdeye.responddto.UserProfileResponse;
import com.booking.thirdeye.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Module", description = "Operations related to user management")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Get all users", description = "Fetches all the users in the system.")
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @Operation(summary = "Get user profile by ID", description = "Fetches the profile details of a user by user ID.")
    @GetMapping("profile/{userId}")
    public ResponseEntity<UserProfileResponse> getUserProfileById(
            @Parameter(description = "ID of the user whose profile is to be fetched") @PathVariable Integer userId) {
        Optional<User> userOptional = userService.getUserById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            UserProfileResponse response = UserProfileResponse.builder()
                    .userId(user.getUserId())
                    .userName(user.getUsername())
                    .email(user.getEmail())
                    .password(user.getPassword())
                    .isVerified(user.getIsVerified())
                    .role(user.getRole())
                    .build();

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(404).build();
        }
    }

    @Operation(summary = "Get user by ID", description = "Fetches user details by user ID.")
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(
            @Parameter(description = "ID of the user to be fetched") @PathVariable Integer id) {
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok().body(user))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new user", description = "Creates a new user in the system.")
    @PostMapping
    public ResponseEntity<User> createUser(
            @Parameter(description = "Details of the user to be created") @RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.status(201).body(createdUser);
    }

    @Operation(summary = "Update user details", description = "Updates the details of an existing user by user ID.")
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @Parameter(description = "ID of the user to be updated") @PathVariable Integer id,
            @Parameter(description = "Updated user details") @RequestBody User userDetails) {
        try {
            User updatedUser = userService.updateUser(id, userDetails);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete user", description = "Deletes an existing user by user ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID of the user to be deleted") @PathVariable Integer id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
