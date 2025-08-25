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
    BookingDto createBooking(BookingDto bookingDto, Long userId);
    
    /**
     * Update an existing booking
     */
    BookingDto updateBooking(Long bookingId, BookingDto bookingDto, Long userId);
    
    /**
     * Find booking by ID
     */
    Optional<BookingDto> findById(Long bookingId);
    
    /**
     * Find all bookings
     */
    List<BookingDto> findAllBookings();
    
    /**
     * Find bookings by user
     */
    List<BookingDto> findBookingsByUser(Long userId);
    
    /**
     * Find bookings by room
     */
    List<BookingDto> findBookingsByRoom(Long roomId);
    
    /**
     * Find bookings by status
     */
    List<BookingDto> findBookingsByStatus(BookingStatus status);
    
    /**
     * Find bookings by date range
     */
    List<BookingDto> findBookingsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Approve a pending booking (Admin only)
     */
    BookingDto approveBooking(Long bookingId, Long adminUserId);
    
    /**
     * Reject a pending booking (Admin only)
     */
    BookingDto rejectBooking(Long bookingId, Long adminUserId, String reason);
    
    /**
     * Cancel a booking (User can cancel their own, Admin can cancel any)
     */
    BookingDto cancelBooking(Long bookingId, Long userId, boolean isAdmin);
    
    /**
     * Check if a room is available for a time period
     */
    boolean isRoomAvailable(Long roomId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * Get booking history for a specific booking
     */
    List<Object> getBookingHistory(Long bookingId);
}
