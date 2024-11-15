package com.booking.thirdeye.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Entity
@Table(name = "Class_Schedule")
public class ClassSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer scheduleId;

    @Column(nullable = false)
    private String className;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private Integer requiredCredits;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private Integer maxSlots;

    @OneToMany(mappedBy = "classSchedule", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Booking> bookings;

}
