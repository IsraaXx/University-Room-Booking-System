package com.sprints.room_booking_system.controller;

import com.sprints.room_booking_system.dto.BookingDto;
import com.sprints.room_booking_system.model.Booking;
import com.sprints.room_booking_system.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    // Request a booking
    @PostMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'FACULTY', 'ADMIN')")
    public ResponseEntity<BookingDto> requestBooking(@Valid @RequestBody BookingDto bookingDto,
                                                  @RequestParam Long userId) {
        BookingDto created = bookingService.createBooking(bookingDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Approve booking (admin)
    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookingDto> approve(@PathVariable("id") Long bookingId,
                                           @RequestParam Long adminUserId) {
        BookingDto approved = bookingService.approveBooking(bookingId, adminUserId);
        return ResponseEntity.ok(approved);
    }

    // Reject booking (admin)
    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookingDto> reject(@PathVariable("id") Long bookingId,
                                          @RequestParam Long adminUserId,
                                          @RequestParam String reason) {
        BookingDto rejected = bookingService.rejectBooking(bookingId, adminUserId, reason);
        return ResponseEntity.ok(rejected);
    }

    // Cancel booking (requester or admin)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('STUDENT', 'FACULTY', 'ADMIN')")
    public ResponseEntity<BookingDto> cancel(@PathVariable("id") Long bookingId,
                                          @RequestParam Long userId,
                                          @RequestParam(defaultValue = "false") boolean isAdmin) {
        BookingDto canceled = bookingService.cancelBooking(bookingId, userId, isAdmin);
        return ResponseEntity.ok(canceled);
    }

    // Booking history
    @GetMapping("/history/{id}")
    @PreAuthorize("hasAnyRole('STUDENT', 'FACULTY', 'ADMIN')")
    public ResponseEntity<List<Object>> history(@PathVariable("id") Long bookingId) {
        List<Object> history = bookingService.getBookingHistory(bookingId);
        return ResponseEntity.ok(history);
    }
}
