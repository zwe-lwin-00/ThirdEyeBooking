package com.booking.thirdeye.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.booking.thirdeye.entity.UserPackage;
import com.booking.thirdeye.service.UserPackageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/userpackage")
@Tag(name = "User Package Module", description = "Operations related to managing user packages")
public class UserPackageController {

    @Autowired
    private UserPackageService userPackageService;

    @Operation(summary = "Get user packages by user ID", description = "Fetches all packages associated with a specific user by their ID.")
    @GetMapping("check/{userId}")
    public ResponseEntity<List<UserPackage>> getUserPackagesByUserId(
            @Parameter(description = "ID of the user whose packages are to be fetched") @PathVariable Integer userId) {

        List<UserPackage> userPackages = userPackageService.findUserPackagesByUserId(userId);

        if (!userPackages.isEmpty()) {
            return ResponseEntity.ok(userPackages);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
