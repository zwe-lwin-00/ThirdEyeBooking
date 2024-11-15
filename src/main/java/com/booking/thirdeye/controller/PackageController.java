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

import com.booking.thirdeye.entity.Package;
import com.booking.thirdeye.service.PackageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/packages")
@Tag(name = "Package Module", description = "Operations related to package management")
public class PackageController {

    @Autowired
    private PackageService packageService;

    @Operation(summary = "Get all packages", description = "Fetches all the packages available in the system.")
    @GetMapping
    public List<Package> getAllPackages() {
        return packageService.getAllPackages();
    }

    @Operation(summary = "Get package by ID", description = "Fetches a specific package by its ID.")
    @GetMapping("/{id}")
    public ResponseEntity<Package> getPackageById(
            @Parameter(description = "ID of the package to be fetched") @PathVariable Integer id) {
        Optional<Package> pkg = packageService.getPackageById(id);
        return pkg.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get packages by country", description = "Fetches a list of packages available in a specific country.")
    @GetMapping("/country/{country}")
    public ResponseEntity<List<Package>> getPackageByCountry(
            @Parameter(description = "Country name to fetch available packages") @PathVariable String country) {
        List<Package> packages = packageService.getPackageByCountry(country);
        if (packages.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(packages);
        }
    }

    @Operation(summary = "Create a new package", description = "Creates a new package in the system.")
    @PostMapping
    public Package createPackage(
            @Parameter(description = "Package details to be created") @RequestBody Package pkg) {
        return packageService.createPackage(pkg);
    }

    @Operation(summary = "Update package", description = "Updates the details of an existing package by its ID.")
    @PutMapping("/{id}")
    public ResponseEntity<Package> updatePackage(
            @Parameter(description = "ID of the package to be updated") @PathVariable Integer id,
            @Parameter(description = "Updated package details") @RequestBody Package updatedPackage) {
        try {
            Package pkg = packageService.updatePackage(id, updatedPackage);
            return ResponseEntity.ok(pkg);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete package", description = "Deletes an existing package by its ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePackage(
            @Parameter(description = "ID of the package to be deleted") @PathVariable Integer id) {
        packageService.deletePackage(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buy a package", description = "Allows a user to purchase a package by providing user and package IDs.")
    @PostMapping("/buy/{userId}/{packageId}")
    public ResponseEntity<String> buyPackage(
            @Parameter(description = "ID of the user purchasing the package") @PathVariable Integer userId,
            @Parameter(description = "ID of the package to be purchased") @PathVariable Integer packageId) {
        String result = packageService.buyPackage(userId, packageId);

        if (result.equals("Package purchased successfully!")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
}
