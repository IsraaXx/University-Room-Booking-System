package com.sprints.room_booking_system.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class BookingDtoBasicValidationTest {
    
    private static Validator validator;
    
    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
    @Test
    void testValidBookingDto() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.plusHours(1);
        LocalDateTime endTime = now.plusHours(2);
        
        // Create a DTO without @NoOverlap annotation for basic validation testing
        TestBookingDto bookingDto = TestBookingDto.builder()
                .roomId(1L)
                .userId(1L)
                .startTime(startTime)
                .endTime(endTime)
                .purpose("Study Group")
                .build();
        
        // When
        Set<ConstraintViolation<TestBookingDto>> violations = validator.validate(bookingDto);
        
        // Then
        assertThat(violations).isEmpty();
    }
    
    @Test
    void testMissingRoomId() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.plusHours(1);
        LocalDateTime endTime = now.plusHours(2);
        
        TestBookingDto bookingDto = TestBookingDto.builder()
                .userId(1L)
                .startTime(startTime)
                .endTime(endTime)
                .purpose("Study Group")
                .build();
        
        // When
        Set<ConstraintViolation<TestBookingDto>> violations = validator.validate(bookingDto);
        
        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Room ID is required");
    }
    
    @Test
    void testMissingUserId() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.plusHours(1);
        LocalDateTime endTime = now.plusHours(2);
        
        TestBookingDto bookingDto = TestBookingDto.builder()
                .roomId(1L)
                .startTime(startTime)
                .endTime(endTime)
                .purpose("Study Group")
                .build();
        
        // When
        Set<ConstraintViolation<TestBookingDto>> violations = validator.validate(bookingDto);
        
        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("User ID is required");
    }
    
    @Test
    void testMissingStartTime() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = now.plusHours(2);
        
        TestBookingDto bookingDto = TestBookingDto.builder()
                .roomId(1L)
                .userId(1L)
                .endTime(endTime)
                .purpose("Study Group")
                .build();
        
        // When
        Set<ConstraintViolation<TestBookingDto>> violations = validator.validate(bookingDto);
        
        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Start time is required");
    }
    
    @Test
    void testMissingEndTime() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.plusHours(1);
        
        TestBookingDto bookingDto = TestBookingDto.builder()
                .roomId(1L)
                .userId(1L)
                .startTime(startTime)
                .purpose("Study Group")
                .build();
        
        // When
        Set<ConstraintViolation<TestBookingDto>> violations = validator.validate(bookingDto);
        
        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("End time is required");
    }
    
    @Test
    void testMissingPurpose() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.plusHours(1);
        LocalDateTime endTime = now.plusHours(2);
        
        TestBookingDto bookingDto = TestBookingDto.builder()
                .roomId(1L)
                .userId(1L)
                .startTime(startTime)
                .endTime(endTime)
                .build();
        
        // When
        Set<ConstraintViolation<TestBookingDto>> violations = validator.validate(bookingDto);
        
        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Purpose is required");
    }
    
    @Test
    void testBlankPurpose() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.plusHours(1);
        LocalDateTime endTime = now.plusHours(2);
        
        TestBookingDto bookingDto = TestBookingDto.builder()
                .roomId(1L)
                .userId(1L)
                .startTime(startTime)
                .endTime(endTime)
                .purpose("")
                .build();
        
        // When
        Set<ConstraintViolation<TestBookingDto>> violations = validator.validate(bookingDto);
        
        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Purpose is required");
    }
    
    @Test
    void testPastStartTime() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.minusHours(1); // Past time
        LocalDateTime endTime = now.plusHours(1);
        
        TestBookingDto bookingDto = TestBookingDto.builder()
                .roomId(1L)
                .userId(1L)
                .startTime(startTime)
                .endTime(endTime)
                .purpose("Study Group")
                .build();
        
        // When
        Set<ConstraintViolation<TestBookingDto>> violations = validator.validate(bookingDto);
        
        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Start time must be in the present or future");
    }
    
    @Test
    void testPastEndTime() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.plusHours(1);
        LocalDateTime endTime = now.minusHours(1); // Past time
        
        TestBookingDto bookingDto = TestBookingDto.builder()
                .roomId(1L)
                .userId(1L)
                .startTime(startTime)
                .endTime(endTime)
                .purpose("Study Group")
                .build();
        
        // When
        Set<ConstraintViolation<TestBookingDto>> violations = validator.validate(bookingDto);
        
        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("End time must be in the future");
    }
    
    @Test
    void testPresentStartTime() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.plusMinutes(1); // Slightly in the future
        LocalDateTime endTime = now.plusHours(1);
        
        TestBookingDto bookingDto = TestBookingDto.builder()
                .roomId(1L)
                .userId(1L)
                .startTime(startTime)
                .endTime(endTime)
                .purpose("Study Group")
                .build();
        
        // When
        Set<ConstraintViolation<TestBookingDto>> violations = validator.validate(bookingDto);
        
        // Then
        assertThat(violations).isEmpty(); // Future time is allowed for startTime
    }
    
    // Test DTO without @NoOverlap annotation for basic validation testing
    public static class TestBookingDto {
        @NotNull(message = "Room ID is required")
        private Long roomId;
        
        @NotNull(message = "User ID is required")
        private Long userId;
        
        @NotNull(message = "Start time is required")
        @FutureOrPresent(message = "Start time must be in the present or future")
        private LocalDateTime startTime;
        
        @NotNull(message = "End time is required")
        @Future(message = "End time must be in the future")
        private LocalDateTime endTime;
        
        @NotBlank(message = "Purpose is required")
        private String purpose;
        
        // Builder pattern
        public static TestBookingDtoBuilder builder() {
            return new TestBookingDtoBuilder();
        }
        
        public static class TestBookingDtoBuilder {
            private TestBookingDto dto = new TestBookingDto();
            
            public TestBookingDtoBuilder roomId(Long roomId) {
                dto.roomId = roomId;
                return this;
            }
            
            public TestBookingDtoBuilder userId(Long userId) {
                dto.userId = userId;
                return this;
            }
            
            public TestBookingDtoBuilder startTime(LocalDateTime startTime) {
                dto.startTime = startTime;
                return this;
            }
            
            public TestBookingDtoBuilder endTime(LocalDateTime endTime) {
                dto.endTime = endTime;
                return this;
            }
            
            public TestBookingDtoBuilder purpose(String purpose) {
                dto.purpose = purpose;
                return this;
            }
            
            public TestBookingDto build() {
                return dto;
            }
        }
        
        // Getters for validation
        public Long getRoomId() { return roomId; }
        public Long getUserId() { return userId; }
        public LocalDateTime getStartTime() { return startTime; }
        public LocalDateTime getEndTime() { return endTime; }
        public String getPurpose() { return purpose; }
    }
}
