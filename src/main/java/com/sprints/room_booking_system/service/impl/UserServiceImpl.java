package com.sprints.room_booking_system.service.impl;

import com.sprints.room_booking_system.dto.UserDto;
import com.sprints.room_booking_system.model.Department;
import com.sprints.room_booking_system.model.User;
import com.sprints.room_booking_system.model.UserRole;
import com.sprints.room_booking_system.repository.DepartmentRepository;
import com.sprints.room_booking_system.repository.UserRepository;
import com.sprints.room_booking_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public UserDto createUser(UserDto userDto) {
        // Check if user with email already exists
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("User with email " + userDto.getEmail() + " already exists");
        }

        // Get department from repository
        Department department = null;
        if (userDto.getDepartmentId() != null) {
            department = departmentRepository.findById(userDto.getDepartmentId())
                    .orElseThrow(() -> new IllegalArgumentException("Department not found with ID: " + userDto.getDepartmentId()));
        }

        User user = User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .role(userDto.getRole())
                .department(department)
                .isActive(true)
                .build();

        User savedUser = userRepository.save(user);
        return toDto(savedUser); // Return DTO
    }
    
    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        if (!existingUser.getEmail().equals(userDto.getEmail()) &&
                userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("User with email " + userDto.getEmail() + " already exists");
        }

        // CORRECTED: Safely check for department changes
        Department newDepartment = existingUser.getDepartment();
        if (userDto.getDepartmentId() != null) {
            // Check if department ID is different, if the old one is null or the IDs don't match
            if (newDepartment == null || !userDto.getDepartmentId().equals(newDepartment.getId())) {
                newDepartment = departmentRepository.findById(userDto.getDepartmentId())
                        .orElseThrow(() -> new IllegalArgumentException("Department not found with ID: " + userDto.getDepartmentId()));
            }
        } else {
            // If the DTO has no department ID, set the new department to null
            newDepartment = null;
        }

        existingUser.setName(userDto.getName());
        existingUser.setEmail(userDto.getEmail());
        existingUser.setRole(userDto.getRole());
        existingUser.setDepartment(newDepartment);

        User updatedUser = userRepository.save(existingUser);
        return toDto(updatedUser);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<UserDto> findById(Long userId) {
        return userRepository.findById(userId).map(this::toDto); // Convert entity to DTO
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<UserDto> findByEmail(String email) {
        return userRepository.findByEmail(email).map(this::toDto); // Convert entity to DTO
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAllUsers() {
        return userRepository.findActiveUsers().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findUsersByRole(UserRole role) {
        return userRepository.findByRole(role).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findUsersByDepartment(Long departmentId) {
        return userRepository.findByDepartmentId(departmentId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        
        user.setIsActive(false);
        userRepository.save(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return findAllUsers();
    }

    @Override
    public UserDto getUserById(Long userId) {
        return findById(userId).get();
    }
    
    @Override
    public void activateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        
        user.setIsActive(true);
        userRepository.save(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private UserDto toDto(User user) {
        return UserDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .isActive(user.getIsActive())
                .role(user.getRole())
                .departmentId(user.getDepartment() != null ? user.getDepartment().getId() : null)
                .build();
    }
}
