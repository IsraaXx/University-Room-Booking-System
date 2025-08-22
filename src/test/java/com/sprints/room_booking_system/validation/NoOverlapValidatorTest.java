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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NoOverlapValidatorTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private NoOverlapValidator validator;

    private BookingDto validBookingDto;
    private BookingDto invalidBookingDto;

    @BeforeEach
    void setUp() {
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = LocalDateTime.now().plusHours(2);

        validBookingDto = BookingDto.builder()
                .roomId(1L)
                .userId(1L)
                .startTime(startTime)
                .endTime(endTime)
                .purpose("Study Group")
                .build();

        invalidBookingDto = BookingDto.builder()
                .roomId(1L)
                .userId(1L)
                .startTime(endTime) // Start time after end time
                .endTime(startTime)
                .purpose("Study Group")
                .build();
    }

    @Test
    void testIsValid_ValidBooking_NoOverlap() {
        // Given
        when(bookingRepository.hasOverlappingBookings(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(false);

        // When
        boolean isValid = validator.isValid(validBookingDto, null);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    void testIsValid_ValidBooking_WithOverlap() {
        // Given
        when(bookingRepository.hasOverlappingBookings(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(true);

        // When
        boolean isValid = validator.isValid(validBookingDto, null);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void testIsValid_InvalidBooking_EndTimeBeforeStartTime() {
        // When
        boolean isValid = validator.isValid(invalidBookingDto, null);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void testIsValid_InvalidBooking_EndTimeEqualsStartTime() {
        // Given
        LocalDateTime time = LocalDateTime.now().plusHours(1);
        BookingDto dto = BookingDto.builder()
                .roomId(1L)
                .userId(1L)
                .startTime(time)
                .endTime(time)
                .purpose("Study Group")
                .build();

        // When
        boolean isValid = validator.isValid(dto, null);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void testIsValid_NullBookingDto() {
        // When
        boolean isValid = validator.isValid(null, null);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    void testIsValid_NullRoomId() {
        // Given
        BookingDto dto = BookingDto.builder()
                .userId(1L)
                .startTime(LocalDateTime.now().plusHours(1))
                .endTime(LocalDateTime.now().plusHours(2))
                .purpose("Study Group")
                .build();

        // When
        boolean isValid = validator.isValid(dto, null);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    void testIsValid_NullStartTime() {
        // Given
        BookingDto dto = BookingDto.builder()
                .roomId(1L)
                .userId(1L)
                .endTime(LocalDateTime.now().plusHours(2))
                .purpose("Study Group")
                .build();

        // When
        boolean isValid = validator.isValid(dto, null);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    void testIsValid_NullEndTime() {
        // Given
        BookingDto dto = BookingDto.builder()
                .roomId(1L)
                .userId(1L)
                .startTime(LocalDateTime.now().plusHours(1))
                .purpose("Study Group")
                .build();

        // When
        boolean isValid = validator.isValid(dto, null);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    void testIsValid_RepositoryException() {
        // Given
        when(bookingRepository.hasOverlappingBookings(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When
        boolean isValid = validator.isValid(validBookingDto, null);

        // Then
        // The validator should handle exceptions gracefully
        assertThat(isValid).isFalse();
    }

    @Test
    void testIsValid_ValidTimeRange() {
        // Given
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = LocalDateTime.now().plusHours(3);
        
        BookingDto dto = BookingDto.builder()
                .roomId(1L)
                .userId(1L)
                .startTime(startTime)
                .endTime(endTime)
                .purpose("Study Group")
                .build();

        when(bookingRepository.hasOverlappingBookings(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(false);

        // When
        boolean isValid = validator.isValid(dto, null);

        // Then
        assertThat(isValid).isTrue();
    }
}
