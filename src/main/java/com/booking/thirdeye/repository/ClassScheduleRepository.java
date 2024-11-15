package com.booking.thirdeye.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.booking.thirdeye.entity.ClassSchedule;

@Repository
public interface ClassScheduleRepository extends JpaRepository<ClassSchedule, Integer> {
    List<ClassSchedule> findByCountry(String country);

    List<ClassSchedule> findByEndTimeBefore(LocalDateTime endTime);
}
