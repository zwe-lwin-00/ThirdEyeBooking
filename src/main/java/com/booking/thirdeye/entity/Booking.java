package com.booking.thirdeye.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Booking")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bookingId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private ClassSchedule classSchedule;

    @Column(nullable = false)
    private String status = "booked";

    @Column(nullable = false)
    private LocalDateTime bookingTime = LocalDateTime.now();

    @Column(nullable = false)
    private boolean checkedIn = false;

}
