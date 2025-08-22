package com.sprints.room_booking_system.validation;

import com.sprints.room_booking_system.dto.BookingDto;
import com.sprints.room_booking_system.repository.BookingRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingDtoIntegrationTest {
    
    private static Validator validator;
    
    @Mock
    private BookingRepository bookingRepository;
    
    private NoOverlapValidator noOverlapValidator;
    
    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
    @BeforeEach
    void setUp() {
        noOverlapValidator = new NoOverlapValidator();
        // Inject the mocked repository into the validator
        ReflectionTestUtils.setField(noOverlapValidator, "bookingRepository", bookingRepository);
    }
    
    @Test
    void testCompleteValidationWithNoOverlap() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.plusHours(1);
        LocalDateTime endTime = now.plusHours(2);
        
        BookingDto bookingDto = BookingDto.builder()
                .roomId(1L)
                .userId(1L)
                .startTime(startTime)
                .endTime(endTime)
                .purpose("Study Group")
                .build();
        
        when(bookingRepository.hasOverlappingBookings(eq(1L), eq(startTime), eq(endTime)))
                .thenReturn(false);
        
        // When
        Set<ConstraintViolation<BookingDto>> violations = validator.validate(bookingDto);
        
        // Then
        assertThat(violations).isEmpty();
    }
    
    @Test
    void testCompleteValidationWithOverlap() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.plusHours(1);
        LocalDateTime endTime = now.plusHours(2);
        
        BookingDto bookingDto = BookingDto.builder()
                .roomId(1L)
                .userId(1L)
                .startTime(startTime)
                .endTime(endTime)
                .purpose("Study Group")
                .build();
        
        when(bookingRepository.hasOverlappingBookings(eq(1L), eq(startTime), eq(endTime)))
                .thenReturn(true);
        
        // When
        Set<ConstraintViolation<BookingDto>> violations = validator.validate(bookingDto);
        
        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<BookingDto> violation = violations.iterator().next();
        assertThat(violation.getMessage()).isEqualTo("The requested time slot overlaps with an existing booking");
        assertThat(violation.getConstraintDescriptor().getAnnotation().annotationType())
                .isEqualTo(NoOverlap.class);
    }
    
    @Test
    void testValidationCombinesMultipleConstraints() {
        // Given - Invalid DTO with multiple issues
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.minusHours(1); // Past time
        LocalDateTime endTime = now.minusMinutes(30); // Past time
        
        BookingDto bookingDto = BookingDto.builder()
                .roomId(1L)
                .userId(1L)
                .startTime(startTime)
                .endTime(endTime)
                .purpose("") // Blank purpose
                .build();
        
        // When
        Set<ConstraintViolation<BookingDto>> violations = validator.validate(bookingDto);
        
        // Then - Should have multiple violations
        assertThat(violations).hasSize(3);
        
        // Check that we have violations for different constraints
        boolean hasStartTimeViolation = violations.stream()
                .anyMatch(v -> v.getMessage().contains("Start time must be in the present or future"));
        boolean hasEndTimeViolation = violations.stream()
                .anyMatch(v -> v.getMessage().contains("End time must be in the future"));
        boolean hasPurposeViolation = violations.stream()
                .anyMatch(v -> v.getMessage().contains("Purpose is required"));
        
        assertThat(hasStartTimeViolation).isTrue();
        assertThat(hasEndTimeViolation).isTrue();
        assertThat(hasPurposeViolation).isTrue();
    }
    
    @Test
    void testCustomValidatorIntegration() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.plusHours(1);
        LocalDateTime endTime = now.plusHours(2);
        
        BookingDto bookingDto = BookingDto.builder()
                .roomId(1L)
                .userId(1L)
                .startTime(startTime)
                .endTime(endTime)
                .purpose("Study Group")
                .build();
        
        // Test both scenarios: no overlap and overlap
        when(bookingRepository.hasOverlappingBookings(eq(1L), eq(startTime), eq(endTime)))
                .thenReturn(false);
        
        // When - First validation (no overlap)
        Set<ConstraintViolation<BookingDto>> noOverlapViolations = validator.validate(bookingDto);
        assertThat(noOverlapViolations).isEmpty();
        
        // Change mock to return overlap
        when(bookingRepository.hasOverlappingBookings(eq(1L), eq(startTime), eq(endTime)))
                .thenReturn(true);
        
        // When - Second validation (with overlap)
        Set<ConstraintViolation<BookingDto>> overlapViolations = validator.validate(bookingDto);
        
        // Then
        assertThat(overlapViolations).hasSize(1);
        ConstraintViolation<BookingDto> violation = overlapViolations.iterator().next();
        assertThat(violation.getMessage()).isEqualTo("The requested time slot overlaps with an existing booking");
    }
}
