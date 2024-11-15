package com.booking.thirdeye.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.booking.thirdeye.entity.Package;
import com.booking.thirdeye.entity.User;
import com.booking.thirdeye.entity.UserPackage;

@Repository
public interface UserPackageRepository extends JpaRepository<UserPackage, Integer> {

    Optional<UserPackage> findByUserAndPkgAndStatus(User user, Package pkg, String status);

    List<UserPackage> findByUser_UserIdAndStatus(Integer userId, String status);

    Optional<UserPackage> findByUserAndStatus(User user, String status);

    Optional<UserPackage> findByUserUserIdAndStatus(Integer userId, String status);

    List<UserPackage> findByUser_UserId(Integer userId);

}
