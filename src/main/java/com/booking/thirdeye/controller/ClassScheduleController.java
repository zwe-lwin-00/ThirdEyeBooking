package com.booking.thirdeye.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.booking.thirdeye.entity.ClassSchedule;
import com.booking.thirdeye.service.ClassScheduleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/class-schedules")
@Tag(name = "Class Schedule Module", description = "Operations related to class schedule management")
public class ClassScheduleController {

    @Autowired
    private ClassScheduleService classScheduleService;

    @Operation(summary = "Get all class schedules", description = "Fetches all the class schedules available.")
    @GetMapping
    public List<ClassSchedule> getAllClassSchedules() {
        return classScheduleService.getAllClassSchedules();
    }

    @Operation(summary = "Get class schedule by ID", description = "Fetches the class schedule for a specific ID.")
    @GetMapping("/{id}")
    public ResponseEntity<ClassSchedule> getClassScheduleById(
            @Parameter(description = "ID of the class schedule to be fetched") @PathVariable Integer id) {
        Optional<ClassSchedule> schedule = classScheduleService.getClassScheduleById(id);
        return schedule.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new class schedule", description = "Creates a new class schedule.")
    @PostMapping
    public ClassSchedule createClassSchedule(
            @Parameter(description = "Class schedule details to be created") @RequestBody ClassSchedule classSchedule) {
        return classScheduleService.createClassSchedule(classSchedule);
    }

    @Operation(summary = "Update an existing class schedule", description = "Updates a class schedule by its ID.")
    @PutMapping("/{id}")
    public ResponseEntity<ClassSchedule> updateClassSchedule(
            @Parameter(description = "ID of the class schedule to be updated") @PathVariable Integer id,
            @Parameter(description = "Updated class schedule details") @RequestBody ClassSchedule updatedClassSchedule) {
        try {
            ClassSchedule schedule = classScheduleService.updateClassSchedule(id, updatedClassSchedule);
            return ResponseEntity.ok(schedule);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete a class schedule", description = "Deletes the class schedule by its ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClassSchedule(
            @Parameter(description = "ID of the class schedule to be deleted") @PathVariable Integer id) {
        classScheduleService.deleteClassSchedule(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get class schedules by country", description = "Fetches the class schedules based on the country.")
    @GetMapping("check/{country}")
    public ResponseEntity<List<ClassSchedule>> getClassScheduleByCountry(
            @Parameter(description = "Country for which class schedules are to be fetched") @PathVariable String country) {
        List<ClassSchedule> classSchedules = classScheduleService.findClassScheduleByCountry(country);
        if (!classSchedules.isEmpty()) {
            return ResponseEntity.ok(classSchedules);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
