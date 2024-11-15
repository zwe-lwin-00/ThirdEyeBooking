package com.booking.thirdeye.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.booking.thirdeye.service.BookingService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/bookings")
@Tag(name = "Booking Module", description = "Operations related to booking management")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Operation(summary = "Create a booking", description = "Creates a booking for a user with the specified schedule.")
    @PostMapping("/createbooking/{userId}/{scheduleId}")
    public ResponseEntity<String> createBooking(
            @Parameter(description = "ID of the user making the booking") @PathVariable Integer userId,
            @Parameter(description = "ID of the schedule being booked") @PathVariable Integer scheduleId) {

        String result = bookingService.createBooking(userId, scheduleId);

        if (result.equals("Booking purchased successfully!")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @Operation(summary = "Cancel a booking", description = "Cancels a booking for a user with the specified schedule.")
    @PostMapping("/cancelbooking/{userId}/{scheduleId}")
    public ResponseEntity<String> cancelBooking(
            @Parameter(description = "ID of the user canceling the booking") @PathVariable Integer userId,
            @Parameter(description = "ID of the schedule to be canceled") @PathVariable Integer scheduleId) {

        String result = bookingService.cancelBooking(userId, scheduleId);

        if (result.equals("Booking cancel successfully!")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @Operation(summary = "Check in to a booking", description = "Checks in a user to their booking with the specified booking ID.")
    @PostMapping("/checkin/{userId}/{bookingId}")
    public ResponseEntity<String> checkinBooking(
            @Parameter(description = "ID of the user checking in") @PathVariable Integer userId,
            @Parameter(description = "ID of the booking being checked in") @PathVariable Integer bookingId) {

        String result = bookingService.checkinBooking(userId, bookingId);

        if (result.equals("Booking Checkin successfully!")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
}