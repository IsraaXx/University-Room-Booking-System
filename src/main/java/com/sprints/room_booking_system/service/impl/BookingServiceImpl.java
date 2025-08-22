package com.sprints.room_booking_system.service.impl;

import com.sprints.room_booking_system.dto.BookingDto;
import com.sprints.room_booking_system.exception.BookingConflictException;
import com.sprints.room_booking_system.exception.InvalidBookingDateException;
import com.sprints.room_booking_system.exception.UnauthorizedOperationException;
import com.sprints.room_booking_system.model.*;
import com.sprints.room_booking_system.repository.BookingHistoryRepository;
import com.sprints.room_booking_system.repository.BookingRepository;
import com.sprints.room_booking_system.repository.HolidayRepository;
import com.sprints.room_booking_system.repository.RoomRepository;
import com.sprints.room_booking_system.repository.UserRepository;
import com.sprints.room_booking_system.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {
    
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final HolidayRepository holidayRepository;
    private final BookingHistoryRepository bookingHistoryRepository;
    
    private static final int MAX_BOOKING_WINDOW_DAYS = 90;
    
    @Override
    public Booking createBooking(BookingDto bookingDto, Long userId) {
        // Validate booking dates
        validateBookingDates(bookingDto.getStartTime(), bookingDto.getEndTime());
        
        // Check if room is available
        if (!isRoomAvailable(bookingDto.getRoomId(), bookingDto.getStartTime(), bookingDto.getEndTime())) {
            throw new BookingConflictException("Room is not available for the specified time period");
        }
        
        // Check for holidays
        validateNoHolidayBooking(bookingDto.getStartTime(), bookingDto.getEndTime());
        
        // Get user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        
        // Get room
        Room room = roomRepository.findById(bookingDto.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found with ID: " + bookingDto.getRoomId()));
        
        // Create booking with PENDING status
        Booking booking = Booking.builder()
                .room(room)
                .user(user)
                .startTime(bookingDto.getStartTime())
                .endTime(bookingDto.getEndTime())
                .purpose(bookingDto.getPurpose())
                .status(BookingStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        
        Booking savedBooking = bookingRepository.save(booking);
        
        // Log booking creation
        logBookingHistory(savedBooking, "CREATED", "Booking created", user);
        
        return savedBooking;
    }
    
    @Override
    public Booking updateBooking(Long bookingId, BookingDto bookingDto, Long userId) {
        Booking existingBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + bookingId));
        
        // Check if user can update this booking
        if (!existingBooking.getUser().getId().equals(userId)) {
            throw new UnauthorizedOperationException("User can only update their own bookings");
        }
        
        // Check if booking can be updated (only PENDING bookings can be updated)
        if (existingBooking.getStatus() != BookingStatus.PENDING) {
            throw new UnauthorizedOperationException("Only PENDING bookings can be updated");
        }
        
        // Validate new dates
        validateBookingDates(bookingDto.getStartTime(), bookingDto.getEndTime());
        
        // Check if room is available for new time (excluding current booking)
        if (!isRoomAvailableForUpdate(bookingDto.getRoomId(), bookingDto.getStartTime(), 
                                    bookingDto.getEndTime(), bookingId)) {
            throw new BookingConflictException("Room is not available for the specified time period");
        }
        
        // Check for holidays
        validateNoHolidayBooking(bookingDto.getStartTime(), bookingDto.getEndTime());
        
        // Update booking
        existingBooking.setStartTime(bookingDto.getStartTime());
        existingBooking.setEndTime(bookingDto.getEndTime());
        existingBooking.setPurpose(bookingDto.getPurpose());
        
        Booking updatedBooking = bookingRepository.save(existingBooking);
        
        // Log booking update
        logBookingHistory(updatedBooking, "UPDATED", "Booking updated", userRepository.findById(userId).orElse(null));
        
        return updatedBooking;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Booking> findById(Long bookingId) {
        return bookingRepository.findById(bookingId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Booking> findAllBookings() {
        return bookingRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Booking> findBookingsByUser(Long userId) {
        return bookingRepository.findByUserId(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Booking> findBookingsByRoom(Long roomId) {
        return bookingRepository.findByRoomId(roomId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Booking> findBookingsByStatus(BookingStatus status) {
        return bookingRepository.findByStatus(status);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Booking> findBookingsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return bookingRepository.findByDateRange(startDate, endDate);
    }
    
    @Override
    public Booking approveBooking(Long bookingId, Long adminUserId) {
        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new IllegalArgumentException("Admin user not found"));
        
        // Check if user is admin
        if (admin.getRole() != UserRole.ADMIN) {
            throw new UnauthorizedOperationException("Only admins can approve bookings");
        }
        
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + bookingId));
        
        // Check if booking can be approved
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalArgumentException("Only PENDING bookings can be approved");
        }
        
        // Check if room is still available
        if (!isRoomAvailable(booking.getRoom().getId(), booking.getStartTime(), booking.getEndTime())) {
            throw new BookingConflictException("Room is no longer available for the specified time period");
        }
        
        // Approve booking
        booking.setStatus(BookingStatus.APPROVED);
        Booking approvedBooking = bookingRepository.save(booking);
        
        // Log approval
        logBookingHistory(approvedBooking, "APPROVED", "Booking approved by admin", admin);
        
        return approvedBooking;
    }
    
    @Override
    public Booking rejectBooking(Long bookingId, Long adminUserId, String reason) {
        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new IllegalArgumentException("Admin user not found"));
        
        // Check if user is admin
        if (admin.getRole() != UserRole.ADMIN) {
            throw new UnauthorizedOperationException("Only admins can reject bookings");
        }
        
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + bookingId));
        
        // Check if booking can be rejected
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalArgumentException("Only PENDING bookings can be rejected");
        }
        
        // Reject booking
        booking.setStatus(BookingStatus.REJECTED);
        Booking rejectedBooking = bookingRepository.save(booking);
        
        // Log rejection
        logBookingHistory(rejectedBooking, "REJECTED", "Booking rejected: " + reason, admin);
        
        return rejectedBooking;
    }
    
    @Override
    public Booking cancelBooking(Long bookingId, Long userId, boolean isAdmin) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + bookingId));
        
        // Check if user can cancel this booking
        if (!isAdmin && !booking.getUser().getId().equals(userId)) {
            throw new UnauthorizedOperationException("User can only cancel their own bookings");
        }
        
        // Check if booking can be cancelled
        if (booking.getStatus() == BookingStatus.CANCELLED || 
            booking.getStatus() == BookingStatus.REJECTED) {
            throw new IllegalArgumentException("Booking cannot be cancelled in current status: " + booking.getStatus());
        }
        
        // Regular users can only cancel before start time
        if (!isAdmin && LocalDateTime.now().isAfter(booking.getStartTime())) {
            throw new UnauthorizedOperationException("Cannot cancel booking after start time");
        }
        
        // Cancel booking
        booking.setStatus(BookingStatus.CANCELLED);
        Booking cancelledBooking = bookingRepository.save(booking);
        
        // Log cancellation
        String action = isAdmin ? "CANCELLED_BY_ADMIN" : "CANCELLED";
        String message = isAdmin ? "Booking cancelled by admin" : "Booking cancelled by user";
        logBookingHistory(cancelledBooking, action, message, user);
        
        return cancelledBooking;
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isRoomAvailable(Long roomId, LocalDateTime startTime, LocalDateTime endTime) {
        return !bookingRepository.hasOverlappingBookings(roomId, startTime, endTime);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Object> getBookingHistory(Long bookingId) {
        return bookingHistoryRepository.findByBookingId(bookingId)
                .stream()
                .map(history -> Map.of(
                    "action", history.getAction(),
                    "actionTime", history.getActionTime(),
                    "reason", history.getReason(),
                    "user", history.getUser().getName()
                ))
                .collect(Collectors.toList());
    }
    
    // Private helper methods
    
    private void validateBookingDates(LocalDateTime startTime, LocalDateTime endTime) {
        LocalDateTime now = LocalDateTime.now();
        
        // Check if start time is in the past
        if (startTime.isBefore(now)) {
            throw new InvalidBookingDateException("Start time cannot be in the past");
        }
        
        // Check if end time is before start time
        if (endTime.isBefore(startTime) || endTime.isEqual(startTime)) {
            throw new InvalidBookingDateException("End time must be after start time");
        }
        
        // Check if booking window exceeds maximum
        if (startTime.isAfter(now.plusDays(MAX_BOOKING_WINDOW_DAYS))) {
            throw new InvalidBookingDateException("Booking cannot be made more than " + MAX_BOOKING_WINDOW_DAYS + " days in advance");
        }
    }
    
    private void validateNoHolidayBooking(LocalDateTime startTime, LocalDateTime endTime) {
        LocalDate startDate = startTime.toLocalDate();
        LocalDate endDate = endTime.toLocalDate();
        
        // Check for holidays in the booking period
        List<Holiday> holidays = holidayRepository.findByDateRange(startDate, endDate);
        if (!holidays.isEmpty()) {
            throw new InvalidBookingDateException("Cannot book on holidays: " + 
                holidays.stream().map(Holiday::getName).collect(Collectors.joining(", ")));
        }
    }
    
    private boolean isRoomAvailableForUpdate(Long roomId, LocalDateTime startTime, LocalDateTime endTime, Long excludeBookingId) {
        return !bookingRepository.hasOverlappingBookings(roomId, startTime, endTime, excludeBookingId);
    }
    
    private void logBookingHistory(Booking booking, String action, String reason, User user) {
        BookingHistory history = BookingHistory.builder()
                .booking(booking)
                .user(user)
                .action(action)
                .reason(reason)
                .actionTime(LocalDateTime.now())
                .build();
        
        bookingHistoryRepository.save(history);
    }
}
