package com.sprints.room_booking_system.service;

import com.sprints.room_booking_system.dto.UserDto;
import com.sprints.room_booking_system.model.User;
import com.sprints.room_booking_system.model.UserRole;

import java.util.List;
import java.util.Optional;

public interface UserService {
    
    /**
     * Create a new user
     */
    UserDto createUser(UserDto userDto);
    
    /**
     * Update an existing user
     */
    UserDto updateUser(Long userId, UserDto userDto);
    
    /**
     * Find user by ID
     */
    Optional<UserDto> findById(Long userId);
    
    /**
     * Find user by email
     */
    Optional<UserDto> findByEmail(String email);
    
    /**
     * Find all users
     */
    List<UserDto> findAllUsers();

    /**
     * Get all users (alias for findAllUsers)
     */
    List<UserDto> getAllUsers();
    
    /**
     * Get user by ID
     */
    UserDto getUserById(Long userId);
    
    /**
     * Find users by role
     */
    List<UserDto> findUsersByRole(UserRole role);
    
    /**
     * Find users by department
     */
    List<UserDto> findUsersByDepartment(Long departmentId);
    
    /**
     * Deactivate user
     */
    void deactivateUser(Long userId);
    
    /**
     * Activate user
     */
    void activateUser(Long userId);
    
    /**
     * Check if user exists by email
     */
    boolean existsByEmail(String email);
}
