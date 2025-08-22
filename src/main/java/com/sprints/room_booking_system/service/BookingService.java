package com.sprints.room_booking_system.service;

import com.sprints.room_booking_system.dto.BookingDto;
import com.sprints.room_booking_system.model.Booking;
import com.sprints.room_booking_system.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingService {
    
    /**
     * Create a new booking with business rule validation
     */
    Booking createBooking(BookingDto bookingDto, Long userId);
    
    /**
     * Update an existing booking
     */
    Booking updateBooking(Long bookingId, BookingDto bookingDto, Long userId);
    
    /**
     * Find booking by ID
     */
    Optional<Booking> findById(Long bookingId);
    
    /**
     * Find all bookings
     */
    List<Booking> findAllBookings();
    
    /**
     * Find bookings by user
     */
    List<Booking> findBookingsByUser(Long userId);
    
    /**
     * Find bookings by room
     */
    List<Booking> findBookingsByRoom(Long roomId);
    
    /**
     * Find bookings by status
     */
    List<Booking> findBookingsByStatus(BookingStatus status);
    
    /**
     * Find bookings by date range
     */
    List<Booking> findBookingsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Approve a pending booking (Admin only)
     */
    Booking approveBooking(Long bookingId, Long adminUserId);
    
    /**
     * Reject a pending booking (Admin only)
     */
    Booking rejectBooking(Long bookingId, Long adminUserId, String reason);
    
    /**
     * Cancel a booking (User can cancel their own, Admin can cancel any)
     */
    Booking cancelBooking(Long bookingId, Long userId, boolean isAdmin);
    
    /**
     * Check if a room is available for a time period
     */
    boolean isRoomAvailable(Long roomId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * Get booking history for a specific booking
     */
    List<Object> getBookingHistory(Long bookingId);
}
