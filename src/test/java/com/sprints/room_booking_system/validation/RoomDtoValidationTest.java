package com.sprints.room_booking_system.validation;

import com.sprints.room_booking_system.dto.RoomDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RoomDtoValidationTest {
    
    private static Validator validator;
    
    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
    @Test
    void testValidRoomDto() {
        RoomDto roomDto = RoomDto.builder()
                .name("Room 101")
                .capacity(25)
                .floorNumber(1)
                .buildingId(1L)
                .build();
        
        Set<ConstraintViolation<RoomDto>> violations = validator.validate(roomDto);
        assertThat(violations).isEmpty();
    }
    
    @Test
    void testMissingName() {
        RoomDto roomDto = RoomDto.builder()
                .capacity(25)
                .floorNumber(1)
                .buildingId(1L)
                .build();
        
        Set<ConstraintViolation<RoomDto>> violations = validator.validate(roomDto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Room name is required");
    }
    
    @Test
    void testBlankName() {
        RoomDto roomDto = RoomDto.builder()
                .name("")
                .capacity(25)
                .floorNumber(1)
                .buildingId(1L)
                .build();
        
        Set<ConstraintViolation<RoomDto>> violations = validator.validate(roomDto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Room name is required");
    }
    
    @Test
    void testMissingCapacity() {
        RoomDto roomDto = RoomDto.builder()
                .name("Room 101")
                .floorNumber(1)
                .buildingId(1L)
                .build();
        
        Set<ConstraintViolation<RoomDto>> violations = validator.validate(roomDto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Capacity is required");
    }
    
    @Test
    void testCapacityTooLow() {
        RoomDto roomDto = RoomDto.builder()
                .name("Room 101")
                .capacity(0)
                .floorNumber(1)
                .buildingId(1L)
                .build();
        
        Set<ConstraintViolation<RoomDto>> violations = validator.validate(roomDto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Capacity must be at least 1");
    }
    
    @Test
    void testCapacityTooHigh() {
        RoomDto roomDto = RoomDto.builder()
                .name("Room 101")
                .capacity(201)
                .floorNumber(1)
                .buildingId(1L)
                .build();
        
        Set<ConstraintViolation<RoomDto>> violations = validator.validate(roomDto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Capacity cannot exceed 200");
    }
    
    @Test
    void testMissingFloorNumber() {
        RoomDto roomDto = RoomDto.builder()
                .name("Room 101")
                .capacity(25)
                .buildingId(1L)
                .build();
        
        Set<ConstraintViolation<RoomDto>> violations = validator.validate(roomDto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Floor number is required");
    }
    
    @Test
    void testNegativeFloorNumber() {
        RoomDto roomDto = RoomDto.builder()
                .name("Room 101")
                .capacity(25)
                .floorNumber(-1)
                .buildingId(1L)
                .build();
        
        Set<ConstraintViolation<RoomDto>> violations = validator.validate(roomDto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Floor number cannot be negative");
    }
    
    @Test
    void testMissingBuildingId() {
        RoomDto roomDto = RoomDto.builder()
                .name("Room 101")
                .capacity(25)
                .floorNumber(1)
                .build();
        
        Set<ConstraintViolation<RoomDto>> violations = validator.validate(roomDto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Building ID is required");
    }
}
