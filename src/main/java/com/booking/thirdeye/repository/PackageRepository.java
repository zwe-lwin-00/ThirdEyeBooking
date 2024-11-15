package com.booking.thirdeye.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.booking.thirdeye.entity.Package;

@Repository
public interface PackageRepository extends JpaRepository<Package, Integer> {

    List<Package> findByCountry(String country);

}
