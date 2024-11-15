package com.booking.thirdeye.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.booking.thirdeye.entity.ClassSchedule;
import com.booking.thirdeye.entity.User;
import com.booking.thirdeye.entity.Waitlist;

@Repository
public interface WaitlistRepository extends JpaRepository<Waitlist, Integer> {
    List<Waitlist> findByClassScheduleOrderByAddedTime(ClassSchedule classSchedule);

    List<Waitlist> findByUserAndClassSchedule(User user, ClassSchedule classSchedule);

    List<Waitlist> findByClassSchedule(ClassSchedule classSchedule);

}
