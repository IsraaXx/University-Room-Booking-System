package com.sprints.room_booking_system.validation;

import com.sprints.room_booking_system.dto.BookingDto;
import com.sprints.room_booking_system.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NoOverlapValidatorTest {
    
    @Mock
    private BookingRepository bookingRepository;
    
    @InjectMocks
    private NoOverlapValidator validator;
    
    private LocalDateTime now;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    
    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        startTime = now.plusHours(1);
        endTime = now.plusHours(2);
    }
    
    @Test
    void testValidWhenNoOverlap() {
        // Given
        BookingDto bookingDto = createValidBookingDto();
        when(bookingRepository.hasOverlappingBookings(eq(1L), eq(startTime), eq(endTime)))
                .thenReturn(false);
        
        // When
        boolean isValid = validator.isValid(bookingDto, null);
        
        // Then
        assertThat(isValid).isTrue();
    }
    
    @Test
    void testInvalidWhenOverlapExists() {
        // Given
        BookingDto bookingDto = createValidBookingDto();
        when(bookingRepository.hasOverlappingBookings(eq(1L), eq(startTime), eq(endTime)))
                .thenReturn(true);
        
        // When
        boolean isValid = validator.isValid(bookingDto, null);
        
        // Then
        assertThat(isValid).isFalse();
    }
    
    @Test
    void testValidWhenStartTimeAfterEndTime() {
        // Given
        BookingDto bookingDto = BookingDto.builder()
                .roomId(1L)
                .userId(1L)
                .startTime(endTime) // Start time after end time
                .endTime(startTime)
                .purpose("Study Group")
                .build();
        
        // When
        boolean isValid = validator.isValid(bookingDto, null);
        
        // Then
        assertThat(isValid).isFalse();
    }
    
    @Test
    void testValidWhenStartTimeEqualsEndTime() {
        // Given
        BookingDto bookingDto = BookingDto.builder()
                .roomId(1L)
                .userId(1L)
                .startTime(startTime)
                .endTime(startTime) // Same time
                .purpose("Study Group")
                .build();
        
        // When
        boolean isValid = validator.isValid(bookingDto, null);
        
        // Then
        assertThat(isValid).isFalse();
    }
    
    @Test
    void testValidWhenNullDto() {
        // When
        boolean isValid = validator.isValid(null, null);
        
        // Then
        assertThat(isValid).isTrue();
    }
    
    @Test
    void testValidWhenNullRoomId() {
        // Given
        BookingDto bookingDto = BookingDto.builder()
                .userId(1L)
                .startTime(startTime)
                .endTime(endTime)
                .purpose("Study Group")
                .build();
        
        // When
        boolean isValid = validator.isValid(bookingDto, null);
        
        // Then
        assertThat(isValid).isTrue();
    }
    
    @Test
    void testValidWhenNullStartTime() {
        // Given
        BookingDto bookingDto = BookingDto.builder()
                .roomId(1L)
                .userId(1L)
                .endTime(endTime)
                .purpose("Study Group")
                .build();
        
        // When
        boolean isValid = validator.isValid(bookingDto, null);
        
        // Then
        assertThat(isValid).isTrue();
    }
    
    @Test
    void testValidWhenNullEndTime() {
        // Given
        BookingDto bookingDto = BookingDto.builder()
                .roomId(1L)
                .userId(1L)
                .startTime(startTime)
                .purpose("Study Group")
                .build();
        
        // When
        boolean isValid = validator.isValid(bookingDto, null);
        
        // Then
        assertThat(isValid).isTrue();
    }
    
    @Test
    void testValidWhenValidTimeRange() {
        // Given
        BookingDto bookingDto = createValidBookingDto();
        when(bookingRepository.hasOverlappingBookings(eq(1L), eq(startTime), eq(endTime)))
                .thenReturn(false);
        
        // When
        boolean isValid = validator.isValid(bookingDto, null);
        
        // Then
        assertThat(isValid).isTrue();
    }
    
    @Test
    void testRepositoryMethodCalledWithCorrectParameters() {
        // Given
        BookingDto bookingDto = createValidBookingDto();
        when(bookingRepository.hasOverlappingBookings(eq(1L), eq(startTime), eq(endTime)))
                .thenReturn(false);
        
        // When
        validator.isValid(bookingDto, null);
        
        // Then
        // Mockito will verify the method was called with correct parameters
        // This test ensures the validator properly delegates to the repository
    }
    
    private BookingDto createValidBookingDto() {
        return BookingDto.builder()
                .roomId(1L)
                .userId(1L)
                .startTime(startTime)
                .endTime(endTime)
                .purpose("Study Group")
                .build();
    }
}
