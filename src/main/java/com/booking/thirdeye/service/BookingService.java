package com.booking.thirdeye.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.booking.thirdeye.entity.Booking;
import com.booking.thirdeye.entity.ClassSchedule;
import com.booking.thirdeye.entity.User;
import com.booking.thirdeye.entity.UserPackage;
import com.booking.thirdeye.entity.Waitlist;
import com.booking.thirdeye.repository.BookingRepository;
import com.booking.thirdeye.repository.ClassScheduleRepository;
import com.booking.thirdeye.repository.UserPackageRepository;
import com.booking.thirdeye.repository.UserRepository;
import com.booking.thirdeye.repository.WaitlistRepository;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private ClassScheduleRepository classScheduleRepository;

    @Autowired
    private WaitlistRepository waitlistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPackageRepository userPackageRepository;

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Optional<Booking> getBookingById(Integer id) {
        return bookingRepository.findById(id);
    }

    public Booking createBooking(Booking booking) {
        if (booking.getBookingTime() == null) {
            booking.setBookingTime(LocalDateTime.now());
        }
        return bookingRepository.save(booking);
    }

    public Booking updateBooking(Integer id, Booking updatedBooking) {
        Optional<Booking> existingBooking = bookingRepository.findById(id);

        if (existingBooking.isPresent()) {
            Booking booking = existingBooking.get();
            booking.setStatus(updatedBooking.getStatus());
            booking.setCheckedIn(updatedBooking.isCheckedIn());
            return bookingRepository.save(booking);
        } else {
            throw new RuntimeException("Booking not found");
        }
    }

    public void deleteBooking(Integer id) {
        Optional<Booking> booking = bookingRepository.findById(id);

        if (booking.isPresent()) {
            bookingRepository.delete(booking.get());
        } else {
            throw new RuntimeException("Booking not found");
        }
    }

    public String checkIn(Integer id) {
        Optional<Booking> bookingOptional = bookingRepository.findById(id);

        if (bookingOptional.isPresent()) {
            Booking booking = bookingOptional.get();
            booking.setCheckedIn(true);
            bookingRepository.save(booking);
            return "Check-in successful!";
        } else {
            return "Booking not found!";
        }
    }

    public String createBooking(Integer userId, Integer scheduleId) {

        Optional<User> userOptional = userRepository.findById(userId);
        Optional<ClassSchedule> classscheduleOptional = classScheduleRepository.findById(scheduleId);

        if (userOptional.isEmpty() || classscheduleOptional.isEmpty()) {
            return "User or Class not found!";
        }

        User user = userOptional.get();
        ClassSchedule classSchedule = classscheduleOptional.get();

        String lockKey = "classSchedule:" + scheduleId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            if (lock.tryLock(5, 10, TimeUnit.SECONDS)) {
                Optional<UserPackage> checkuserpackage = userPackageRepository.findByUserAndStatus(user, "ACTIVE");
                if (checkuserpackage.isEmpty()) {
                    return "You do not have active package and buy package first to book a class!";
                }
                UserPackage UserPackage = checkuserpackage.get();

                if (!UserPackage.getPkg().getCountry().equals(classSchedule.getCountry())) {
                    return "Class is not available in the user's country.";
                }

                if (UserPackage.getRemainingCredits() < classSchedule.getRequiredCredits()) {
                    return "Not enough credits to book this class.";
                }

                List<Booking> userBookings = bookingRepository.findByUserAndStatus(user,
                        "booked");
                for (Booking b : userBookings) {
                    if (b.getClassSchedule().getStartTime().isBefore(classSchedule.getEndTime())
                            &&
                            b.getClassSchedule().getEndTime().isAfter(classSchedule.getStartTime())) {
                        return "Cannot book overlapping class time.";
                    }
                }

                if (classSchedule.getMaxSlots() <= 0) {

                    Waitlist waitlist = new Waitlist();
                    waitlist.setUser(user);
                    waitlist.setClassSchedule(classSchedule);
                    waitlistRepository.save(waitlist);

                    UserPackage.setRemainingCredits(
                            UserPackage.getRemainingCredits() - classSchedule.getRequiredCredits());
                    userPackageRepository.save(UserPackage);

                    return "Class is full. You have been added to the waitlist.";

                }

                UserPackage currentUserPackage = checkuserpackage.get();
                currentUserPackage
                        .setRemainingCredits(
                                currentUserPackage.getRemainingCredits() - classSchedule.getRequiredCredits());

                ClassSchedule currentclassSchedule = classscheduleOptional.get();
                currentclassSchedule.setMaxSlots(currentclassSchedule.getMaxSlots() - 1);
                Booking booking = new Booking();
                booking.setUser(user);
                booking.setClassSchedule(classSchedule);
                booking.setStatus("booked");
                booking.setBookingTime(LocalDateTime.now());
                bookingRepository.save(booking);

                return "Class booking purchased successfully!";
            } else {
                return "System is busy. Please try again.";
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "An error occurred while processing your booking.";
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public String cancelBooking(Integer userId, Integer scheduleId) {
        Optional<User> userOptional = userRepository.findById(userId);
        Optional<ClassSchedule> classScheduleOptional = classScheduleRepository.findById(scheduleId);

        if (userOptional.isEmpty() || classScheduleOptional.isEmpty()) {
            return "User or Class not found!";
        }

        User user = userOptional.get();
        ClassSchedule classSchedule = classScheduleOptional.get();

        Optional<Booking> bookingOptional = bookingRepository.findByUserAndClassScheduleAndStatus(user, classSchedule,
                "booked");

        if (bookingOptional.isEmpty()) {
            return "Booking not found!";
        }

        Booking booking = bookingOptional.get();

        Optional<UserPackage> userPackageOptional = userPackageRepository.findByUserUserIdAndStatus(user.getUserId(),
                "active");

        if (userPackageOptional.isEmpty()) {
            return "User package not found!";
        }

        UserPackage userPackage = userPackageOptional.get();

        LocalDateTime now = LocalDateTime.now();
        classSchedule.setMaxSlots(classSchedule.getMaxSlots() + 1);
        if (now.isBefore(classSchedule.getStartTime().minusHours(4))) {
            userPackage.setRemainingCredits(userPackage.getRemainingCredits() + classSchedule.getRequiredCredits());
            booking.setStatus("canceled");
            bookingRepository.save(booking);

            List<Waitlist> waitlistedUsers = waitlistRepository.findByClassScheduleOrderByAddedTime(classSchedule);
            if (!waitlistedUsers.isEmpty()) {
                Waitlist nextInLine = waitlistedUsers.get(0);
                waitlistRepository.delete(nextInLine);

                createBooking(nextInLine.getUser().getUserId(), scheduleId);

                UserPackage waitlistedUserPackage = userPackageRepository
                        .findByUserUserIdAndStatus(nextInLine.getUser().getUserId(), "active").get();
                waitlistedUserPackage.setRemainingCredits(
                        waitlistedUserPackage.getRemainingCredits() + classSchedule.getRequiredCredits());
                userPackageRepository.save(waitlistedUserPackage);
            }

            return "Booking canceled successfully with refund.";
        } else {
            booking.setStatus("canceled");
            bookingRepository.save(booking);
            return "Booking canceled successfully without refund.";
        }
    }

    public String checkinBooking(Integer userId, Integer bookingId) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);

        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();

            // Check if the class schedule has expired

            if (!booking.getUser().getUserId().equals(userId)) {
                return "User is not associated with this booking.";
            }

            if (!booking.getStatus().equals("booked")) {
                return "Cannot check-in. The booking is not in 'booked' status.";
            }

            if (booking.isCheckedIn()) {
                return "Booking is already checked-in.";
            }

            booking.setCheckedIn(true);
            booking.setStatus("checked-in");
            bookingRepository.save(booking);

            return "Checkin Booking successfully!";
        } else {
            return "Booking not found.";
        }
    }

}
