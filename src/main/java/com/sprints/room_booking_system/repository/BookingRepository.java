package com.sprints.room_booking_system.repository;

import com.sprints.room_booking_system.dto.BookingDto;
import com.sprints.room_booking_system.model.Booking;
import com.sprints.room_booking_system.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    /**
     * Find bookings by user
     */
    List<Booking> findByUserId(Long userId);
    
    /**
     * Find bookings by room
     */
    List<Booking> findByRoomId(Long roomId);
    
    /**
     * Find bookings by status
     */

    List<Booking> findByStatus(BookingStatus status);
    
    /**
     * Find bookings by user and status
     */
    List<Booking> findByUserIdAndStatus(Long userId, BookingStatus status);
    
    /**
     * Find bookings by room and status
     */
    List<Booking> findByRoomIdAndStatus(Long roomId, BookingStatus status);
    
    /**
     * Find bookings by date range
     */
    @Query("SELECT b FROM Booking b WHERE b.startTime >= :startDate AND b.startTime <= :endDate")
    List<Booking> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find bookings by room and date range
     */
    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId AND b.startTime >= :startDate AND b.startTime <= :endDate")
    List<Booking> findByRoomAndDateRange(@Param("roomId") Long roomId,
                                         @Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate);
    
    /**
     * Check if a booking overlaps with existing APPROVED/PENDING bookings
     * This is the key query for conflict detection
     */
    @Query("""
        SELECT COUNT(b) > 0 FROM Booking b 
        WHERE b.room.id = :roomId 
        AND b.status IN (com.sprints.room_booking_system.model.BookingStatus.PENDING,com.sprints.room_booking_system.model.BookingStatus.APPROVED)
        AND b.id != :excludeBookingId
        AND (
            (b.startTime <= :startTime AND b.endTime > :startTime) OR
            (b.startTime < :endTime AND b.endTime >= :endTime) OR
            (b.startTime >= :startTime AND b.endTime <= :endTime)
        )
        """)
    boolean hasOverlappingBookings(@Param("roomId") Long roomId, 
                                   @Param("startTime") LocalDateTime startTime, 
                                   @Param("endTime") LocalDateTime endTime,
                                   @Param("excludeBookingId") Long excludeBookingId);
    
    /**
     * Check if a new booking overlaps with existing APPROVED/PENDING bookings
     */
    @Query("""
        SELECT COUNT(b) > 0 FROM Booking b 
        WHERE b.room.id = :roomId 

        AND b.status IN (com.sprints.room_booking_system.model.BookingStatus.PENDING,com.sprints.room_booking_system.model.BookingStatus.APPROVED)
        AND (
            (b.startTime <= :startTime AND b.endTime > :startTime) OR
            (b.startTime < :endTime AND b.endTime >= :endTime) OR
            (b.startTime >= :startTime AND b.endTime <= :endTime)
        )
        """)
    boolean hasOverlappingBookings(@Param("roomId") Long roomId, 
                                   @Param("startTime") LocalDateTime startTime, 
                                   @Param("endTime") LocalDateTime endTime);
    
    /**
     * Soft delete/cancel booking by updating status
     */
    @Modifying
    @Query("UPDATE Booking b SET b.status = :status WHERE b.id = :bookingId")
    int updateBookingStatus(@Param("bookingId") Long bookingId, @Param("status") BookingStatus status);
    
    /**
     * Find active bookings (PENDING or APPROVED)
     */
    @Query("SELECT b FROM Booking b WHERE b.status IN (com.sprints.room_booking_system.model.BookingStatus.PENDING,com.sprints.room_booking_system.model.BookingStatus.APPROVED)")
    List<Booking> findActiveBookings();
    
    /**
     * Find bookings by room and time range
     * This query finds bookings that overlap with the specified time range
     */
    @Query("""
        SELECT b FROM Booking b 
        WHERE b.room.id = :roomId 
        AND (
            (b.startTime <= :startTime AND b.endTime > :startTime) OR
            (b.startTime < :endTime AND b.endTime >= :endTime) OR
            (b.startTime >= :startTime AND b.endTime <= :endTime)
        )
        """)
    List<Booking> findByRoomAndTimeRange(@Param("roomId") Long roomId,
                                         @Param("startTime") LocalDateTime startTime, 
                                         @Param("endTime") LocalDateTime endTime);
}
