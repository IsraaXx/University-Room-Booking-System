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
    User createUser(UserDto userDto);
    
    /**
     * Update an existing user
     */
    User updateUser(Long userId, UserDto userDto);
    
    /**
     * Find user by ID
     */
    Optional<User> findById(Long userId);
    
    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Find all users
     */
    List<User> findAllUsers();
    
    /**
     * Get all users (alias for findAllUsers)
     */
    List<User> getAllUsers();
    
    /**
     * Get user by ID
     */
    User getUserById(Long userId);
    
    /**
     * Find users by role
     */
    List<User> findUsersByRole(UserRole role);
    
    /**
     * Find users by department
     */
    List<User> findUsersByDepartment(Long departmentId);
    
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
