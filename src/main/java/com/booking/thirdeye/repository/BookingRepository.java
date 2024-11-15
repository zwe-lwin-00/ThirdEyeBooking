package com.booking.thirdeye.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.booking.thirdeye.entity.Booking;
import com.booking.thirdeye.entity.ClassSchedule;
import com.booking.thirdeye.entity.User;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByUserAndStatus(User user, String status);

    Optional<Booking> findByUserAndClassScheduleAndStatus(User user, ClassSchedule classSchedule, String status);

}