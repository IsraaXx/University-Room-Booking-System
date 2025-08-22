package com.sprints.room_booking_system.validation;

import com.sprints.room_booking_system.dto.UserDto;
import com.sprints.room_booking_system.model.UserRole;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserDtoValidationTest {
    
    private static Validator validator;
    
    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
    @Test
    void testValidUserDto() {
        UserDto userDto = UserDto.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .password("password123")
                .role(UserRole.STUDENT)
                .build();
        
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertThat(violations).isEmpty();
    }
    
    @Test
    void testInvalidEmail() {
        UserDto userDto = UserDto.builder()
                .name("John Doe")
                .email("invalid-email")
                .password("password123")
                .role(UserRole.STUDENT)
                .build();
        
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Email must be valid");
    }
    
    @Test
    void testMissingName() {
        UserDto userDto = UserDto.builder()
                .email("john.doe@example.com")
                .password("password123")
                .role(UserRole.STUDENT)
                .build();
        
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Name is required");
    }
    
    @Test
    void testMissingEmail() {
        UserDto userDto = UserDto.builder()
                .name("John Doe")
                .password("password123")
                .role(UserRole.STUDENT)
                .build();
        
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Email is required");
    }
    
    @Test
    void testMissingPassword() {
        UserDto userDto = UserDto.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .role(UserRole.STUDENT)
                .build();
        
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Password is required");
    }
    
    @Test
    void testMissingRole() {
        UserDto userDto = UserDto.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .password("password123")
                .build();
        
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Role is required");
    }
    
    @Test
    void testBlankName() {
        UserDto userDto = UserDto.builder()
                .name("")
                .email("john.doe@example.com")
                .password("password123")
                .role(UserRole.STUDENT)
                .build();
        
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Name is required");
    }
    
    @Test
    void testBlankEmail() {
        UserDto userDto = UserDto.builder()
                .name("John Doe")
                .email("")
                .password("password123")
                .role(UserRole.STUDENT)
                .build();
        
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Email is required");
    }
}
