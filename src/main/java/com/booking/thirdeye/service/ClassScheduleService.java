package com.booking.thirdeye.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.booking.thirdeye.entity.ClassSchedule;
import com.booking.thirdeye.repository.ClassScheduleRepository;

@Service
public class ClassScheduleService {

    @Autowired
    private ClassScheduleRepository classScheduleRepository;

    public List<ClassSchedule> getAllClassSchedules() {
        return classScheduleRepository.findAll();
    }

    public Optional<ClassSchedule> getClassScheduleById(Integer id) {
        return classScheduleRepository.findById(id);
    }

    public ClassSchedule createClassSchedule(ClassSchedule classSchedule) {
        return classScheduleRepository.save(classSchedule);
    }

    public ClassSchedule updateClassSchedule(Integer id, ClassSchedule updatedClassSchedule) {
        return classScheduleRepository.findById(id)
                .map(schedule -> {
                    schedule.setClassName(updatedClassSchedule.getClassName());
                    schedule.setCountry(updatedClassSchedule.getCountry());
                    schedule.setRequiredCredits(updatedClassSchedule.getRequiredCredits());
                    schedule.setStartTime(updatedClassSchedule.getStartTime());
                    schedule.setEndTime(updatedClassSchedule.getEndTime());
                    schedule.setMaxSlots(updatedClassSchedule.getMaxSlots());
                    return classScheduleRepository.save(schedule);
                }).orElseThrow(() -> new RuntimeException("ClassSchedule not found with id " + id));
    }

    public void deleteClassSchedule(Integer id) {
        classScheduleRepository.deleteById(id);
    }

    public List<ClassSchedule> findClassScheduleByCountry(String country) {
        return classScheduleRepository.findByCountry(country);
    }
}
