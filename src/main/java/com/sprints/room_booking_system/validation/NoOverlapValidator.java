package com.sprints.room_booking_system.validation;

import com.sprints.room_booking_system.dto.BookingDto;
import com.sprints.room_booking_system.repository.BookingRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NoOverlapValidator implements ConstraintValidator<NoOverlap, BookingDto> {
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Override
    public boolean isValid(BookingDto bookingDto, ConstraintValidatorContext context) {
        if (bookingDto == null || 
            bookingDto.getRoomId() == null || 
            bookingDto.getStartTime() == null || 
            bookingDto.getEndTime() == null) {
            return true; // Let other validators handle null checks
        }
        
        // Check if start time is before or equal to end time
        if (bookingDto.getStartTime().isAfter(bookingDto.getEndTime()) || 
            bookingDto.getStartTime().isEqual(bookingDto.getEndTime())) {
            return false;
        }
        
        try {
            // Use the repository to check for overlapping bookings
            return !bookingRepository.hasOverlappingBookings(
                bookingDto.getRoomId(),
                bookingDto.getStartTime(),
                bookingDto.getEndTime()
            );
        } catch (Exception e) {
            // If there's any error checking for overlaps, fail validation
            // This is safer than allowing potentially invalid bookings
            return false;
        }
    }
}
