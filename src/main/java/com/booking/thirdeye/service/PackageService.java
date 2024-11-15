package com.booking.thirdeye.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.booking.thirdeye.entity.Package;
import com.booking.thirdeye.entity.User;
import com.booking.thirdeye.entity.UserPackage;
import com.booking.thirdeye.repository.PackageRepository;
import com.booking.thirdeye.repository.UserPackageRepository;
import com.booking.thirdeye.repository.UserRepository;

@Service
public class PackageService {

    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPackageRepository userPackageRepository;

    public List<Package> getAllPackages() {
        return packageRepository.findAll();
    }

    public Optional<Package> getPackageById(Integer id) {
        return packageRepository.findById(id);
    }

    public List<Package> getPackageByCountry(String country) {
        return packageRepository.findByCountry(country);
    }

    public Package createPackage(Package pkg) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureDateTime = now.plusMonths(3);
        pkg.setExpiryDate(futureDateTime);
        return packageRepository.save(pkg);
    }

    public Package updatePackage(Integer id, Package updatedPackage) {
        return packageRepository.findById(id)
                .map(pkg -> {
                    pkg.setName(updatedPackage.getName());
                    pkg.setCredits(updatedPackage.getCredits());
                    pkg.setPrice(updatedPackage.getPrice());
                    pkg.setExpiryDate(updatedPackage.getExpiryDate());
                    pkg.setCountry(updatedPackage.getCountry());
                    return packageRepository.save(pkg);
                }).orElseThrow(() -> new RuntimeException("Package not found with id " + id));
    }

    public void deletePackage(Integer id) {
        packageRepository.deleteById(id);
    }

    public String buyPackage(Integer userId, Integer packageId) {
        Optional<User> userOptional = userRepository.findById(userId);
        Optional<Package> packageOptional = packageRepository.findById(packageId);

        if (userOptional.isEmpty() || packageOptional.isEmpty()) {
            return "User or Package not found!";
        }

        User user = userOptional.get();
        Package pkg = packageOptional.get();

        Optional<UserPackage> existingUserPackage = userPackageRepository
                .findByUserAndPkgAndStatus(user, pkg, "ACTIVE");

        if (existingUserPackage.isPresent()) {

            UserPackage existingPackage = existingUserPackage.get();
            existingPackage.setRemainingCredits(existingPackage.getRemainingCredits() + pkg.getCredits());

            existingPackage.setExpiryDate(pkg.getExpiryDate());

            userPackageRepository.save(existingPackage);

            return "Package credits updated successfully!";

        }

        UserPackage newUserPackage = new UserPackage();
        newUserPackage.setUser(user);
        newUserPackage.setPkg(pkg);
        newUserPackage.setRemainingCredits(pkg.getCredits());
        newUserPackage.setExpiryDate(LocalDateTime.now().plusMonths(1));
        newUserPackage.setStatus("active");

        userPackageRepository.save(newUserPackage);

        return "Package purchased successfully!";
    }
}
