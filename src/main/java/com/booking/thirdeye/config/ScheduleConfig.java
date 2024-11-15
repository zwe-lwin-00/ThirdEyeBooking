package com.booking.thirdeye.config;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.booking.thirdeye.entity.ClassSchedule;
import com.booking.thirdeye.entity.User;
import com.booking.thirdeye.entity.UserPackage;
import com.booking.thirdeye.entity.Waitlist;
import com.booking.thirdeye.repository.BookingRepository;
import com.booking.thirdeye.repository.ClassScheduleRepository;
import com.booking.thirdeye.repository.UserPackageRepository;
import com.booking.thirdeye.repository.WaitlistRepository;

import jakarta.transaction.Transactional;

@Component
public class ScheduleConfig {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserPackageRepository userPackageRepository;

    @Autowired
    private ClassScheduleRepository classScheduleRepository;

    @Autowired
    private WaitlistRepository waitlistRepository;

    @Scheduled(fixedRate = 1800000) // Run every 30 minutes
    public void checkExpiredBookings() {
        bookingRepository.findAll().forEach(booking -> {
            if (booking.getClassSchedule().getEndTime().isBefore(LocalDateTime.now())) {
                booking.setStatus("expired");
                bookingRepository.save(booking);
                System.out.println("Booking ID " + booking.getBookingId() + " is expired");
            }
        });
    }

    @Scheduled(fixedRate = 1800000) // Run every 30 minutes
    public void checkExpiredUserPackage() {
        userPackageRepository.findAll().forEach(userPackage -> {
            if (userPackage.getExpiryDate().isBefore(LocalDateTime.now())) {
                userPackage.setStatus("expired");
                userPackageRepository.save(userPackage);
                System.out.println("UserPackage ID " + userPackage.getUserPackageId() + " is expired");
            }
        });
    }

    @Scheduled(fixedRate = 1800000) // Run every 30 minutes
    @Transactional
    public void refundCreditsForWaitlistedUsers() {
        LocalDateTime now = LocalDateTime.now();

        List<ClassSchedule> completedClasses = classScheduleRepository.findByEndTimeBefore(now);

        for (ClassSchedule classSchedule : completedClasses) {
            List<Waitlist> waitlistedUsers = waitlistRepository.findByClassSchedule(classSchedule);

            for (Waitlist waitlist : waitlistedUsers) {
                User user = waitlist.getUser();

                UserPackage userPackage = userPackageRepository.findByUserUserIdAndStatus(user.getUserId(), "active")
                        .orElse(null);

                if (userPackage != null) {
                    userPackage.setRemainingCredits(
                            userPackage.getRemainingCredits() + classSchedule.getRequiredCredits());
                    userPackageRepository.save(userPackage);
                }

                waitlistRepository.delete(waitlist);
            }
        }
    }
}
