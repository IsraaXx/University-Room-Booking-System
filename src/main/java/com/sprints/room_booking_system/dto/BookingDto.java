package com.sprints.room_booking_system.dto;

import com.sprints.room_booking_system.validation.NoOverlap;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@NoOverlap(message = "The requested time slot overlaps with an existing booking")
public class BookingDto {
    
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
}
