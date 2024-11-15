package com.booking.thirdeye.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Waitlist")
public class Waitlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer waitlistId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private ClassSchedule classSchedule;

    @Column(nullable = false)
    private LocalDateTime addedTime = LocalDateTime.now();

    public Waitlist() {
    }

    public Waitlist(User user, ClassSchedule classSchedule) {
        this.user = user;
        this.classSchedule = classSchedule;
    }
}
