package com.booking.thirdeye.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.booking.thirdeye.entity.UserPackage;
import com.booking.thirdeye.repository.UserPackageRepository;

@Service
public class UserPackageService {

    @Autowired
    private UserPackageRepository userPackageRepository;

    public List<UserPackage> findUserPackagesByUserId(Integer userId) {
        return userPackageRepository.findByUser_UserId(userId);
    }

}
