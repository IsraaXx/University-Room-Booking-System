package com.sprints.room_booking_system.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomDto {
    
    @NotBlank(message = "Room name is required")
    private String name;
    
    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    @Max(value = 200, message = "Capacity cannot exceed 200")
    private Integer capacity;
    
    @NotNull(message = "Floor number is required")
    @Min(value = 0, message = "Floor number cannot be negative")
    private Integer floorNumber;
    
    @NotNull(message = "Building ID is required")
    private Long buildingId;


    private List<Long> featureIds;
    private Boolean isActive;
}
