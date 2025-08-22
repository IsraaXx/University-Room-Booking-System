package com.sprints.room_booking_system.repository;

import com.sprints.room_booking_system.model.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Long> {
    
    /**
     * Find holiday by date
     */
    Optional<Holiday> findByDate(LocalDate date);
    
    /**
     * Find holidays by year
     */
    @Query("SELECT h FROM Holiday h WHERE YEAR(h.date) = :year")
    List<Holiday> findByYear(@Param("year") int year);
    
    /**
     * Find holidays by date range
     */
    @Query("SELECT h FROM Holiday h WHERE h.date >= :startDate AND h.date <= :endDate")
    List<Holiday> findByDateRange(@Param("startDate") LocalDate startDate, 
                                   @Param("endDate") LocalDate endDate);
    
    /**
     * Find holidays by name containing
     */
    List<Holiday> findByNameContainingIgnoreCase(String name);
    
    /**
     * Check if date is a holiday
     */
    boolean existsByDate(LocalDate date);
    
    /**
     * Find holidays in a month
     */
    @Query("SELECT h FROM Holiday h WHERE MONTH(h.date) = :month AND YEAR(h.date) = :year")
    List<Holiday> findByMonthAndYear(@Param("month") int month, @Param("year") int year);
}
