package com.sprints.room_booking_system.repository;

import com.sprints.room_booking_system.model.BookingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingHistoryRepository extends JpaRepository<BookingHistory, Long> {
    
    /**
     * Find history by booking
     */
    List<BookingHistory> findByBookingId(Long bookingId);
    
    /**
     * Find history by user
     */
    List<BookingHistory> findByUserId(Long userId);
    
    /**
     * Find history by action
     */
    List<BookingHistory> findByAction(String action);
    
    /**
     * Find history by date range
     */
    @Query("SELECT bh FROM BookingHistory bh WHERE bh.actionTime >= :startDate AND bh.actionTime <= :endDate")
    List<BookingHistory> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find history by booking and action
     */
    List<BookingHistory> findByBookingIdAndAction(Long bookingId, String action);
    
    /**
     * Find history by user and date range
     */
    @Query("SELECT bh FROM BookingHistory bh WHERE bh.user.id = :userId AND bh.actionTime >= :startDate AND bh.actionTime <= :endDate")
    List<BookingHistory> findByUserAndDateRange(@Param("userId") Long userId, 
                                                @Param("startDate") LocalDateTime startDate, 
                                                @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find recent history for a booking
     */
    @Query("SELECT bh FROM BookingHistory bh WHERE bh.booking.id = :bookingId ORDER BY bh.actionTime DESC")
    List<BookingHistory> findRecentHistoryByBooking(@Param("bookingId") Long bookingId);
}
