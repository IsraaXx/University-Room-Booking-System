package com.sprints.room_booking_system.service;

import com.sprints.room_booking_system.dto.UserDto;
import com.sprints.room_booking_system.model.Department;
import com.sprints.room_booking_system.model.User;
import com.sprints.room_booking_system.model.UserRole;
import com.sprints.room_booking_system.repository.UserRepository;
import com.sprints.room_booking_system.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserServiceImpl userService;
    
    private UserDto userDto;
    private User user;
    private Department department;
    
    @BeforeEach
    void setUp() {
        department = Department.builder()
                .id(1L)
                .name("Computer Science")
                .build();
        
        userDto = UserDto.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .password("password123")
                .role(UserRole.STUDENT)
                .departmentId(1L)
                .build();
        
        user = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .password("password123")
                .role(UserRole.STUDENT)
                .department(department)
                .isActive(true)
                .build();
    }
    
    @Test
    void testCreateUser_Success() {
        // Given
        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);
        
        // When
        UserDto createdUser = userService.createUser(userDto);
        
        // Then
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getName()).isEqualTo("John Doe");
        assertThat(createdUser.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(createdUser.getRole()).isEqualTo(UserRole.STUDENT);
        assertThat(createdUser.getIsActive()).isTrue();
        
        verify(userRepository).existsByEmail(userDto.getEmail());
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void testCreateUser_EmailAlreadyExists() {
        // Given
        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(true);
        
        // When & Then
        assertThatThrownBy(() -> userService.createUser(userDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User with email john.doe@example.com already exists");
        
        verify(userRepository).existsByEmail(userDto.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void testUpdateUser_Success() {
        // Given
        User existingUser = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .password("password123")
                .role(UserRole.STUDENT)
                .department(department)
                .isActive(true)
                .build();
        
        UserDto updateDto = UserDto.builder()
                .name("John Updated")
                .email("john.updated@example.com")
                .password("newpassword")
                .role(UserRole.FACULTY)
                .build();
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail(updateDto.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(existingUser);
        
        // When
        UserDto updatedUser = userService.updateUser(1L, updateDto);
        
        // Then
        assertThat(updatedUser).isNotNull();
        verify(userRepository).findById(1L);
        verify(userRepository).existsByEmail(updateDto.getEmail());
        verify(userRepository).save(existingUser);
    }
    
    @Test
    void testUpdateUser_UserNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> userService.updateUser(1L, userDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found with ID: 1");
        
        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void testUpdateUser_EmailAlreadyExists() {
        // Given
        User existingUser = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .password("password123")
                .role(UserRole.STUDENT)
                .department(department)
                .isActive(true)
                .build();
        
        UserDto updateDto = UserDto.builder()
                .name("John Updated")
                .email("new.email@example.com")
                .password("newpassword")
                .role(UserRole.FACULTY)
                .build();
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail(updateDto.getEmail())).thenReturn(true);
        
        // When & Then
        assertThatThrownBy(() -> userService.updateUser(1L, updateDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User with email new.email@example.com already exists");
        
        verify(userRepository).findById(1L);
        verify(userRepository).existsByEmail(updateDto.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void testFindById_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        
        // When
        Optional<UserDto> foundUser = userService.findById(1L);
        
        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("John Doe");
        verify(userRepository).findById(1L);
    }
    
    @Test
    void testFindById_NotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When
        Optional<UserDto> foundUser = userService.findById(1L);
        
        // Then
        assertThat(foundUser).isEmpty();
        verify(userRepository).findById(1L);
    }
    
    @Test
    void testFindByEmail_Success() {
        // Given
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));
        
        // When
        Optional<UserDto> foundUser = userService.findByEmail("john.doe@example.com");
        
        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("john.doe@example.com");
        verify(userRepository).findByEmail("john.doe@example.com");
    }
    
    @Test
    void testFindAllUsers_Success() {
        // Given
        List<User> users = Arrays.asList(user);
        when(userRepository.findActiveUsers()).thenReturn(users);
        
        // When
        List<UserDto> allUsers = userService.findAllUsers();
        
        // Then
        assertThat(allUsers).hasSize(1);
        assertThat(allUsers.get(0).getName()).isEqualTo("John Doe");
        verify(userRepository).findActiveUsers();
    }
    
    @Test
    void testFindUsersByRole_Success() {
        // Given
        List<User> students = Arrays.asList(user);
        when(userRepository.findByRole(UserRole.STUDENT)).thenReturn(students);
        
        // When
        List<UserDto> usersByRole = userService.findUsersByRole(UserRole.STUDENT);
        
        // Then
        assertThat(usersByRole).hasSize(1);
        assertThat(usersByRole.get(0).getRole()).isEqualTo(UserRole.STUDENT);
        verify(userRepository).findByRole(UserRole.STUDENT);
    }
    
    @Test
    void testFindUsersByDepartment_Success() {
        // Given
        List<User> departmentUsers = Arrays.asList(user);
        when(userRepository.findByDepartmentId(1L)).thenReturn(departmentUsers);
        
        // When
        List<UserDto> usersByDepartment = userService.findUsersByDepartment(1L);
        
        // Then
        assertThat(usersByDepartment).hasSize(1);
        verify(userRepository).findByDepartmentId(1L);
    }
    
    @Test
    void testDeactivateUser_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        
        // When
        userService.deactivateUser(1L);
        
        // Then
        assertThat(user.getIsActive()).isFalse();
        verify(userRepository).findById(1L);
        verify(userRepository).save(user);
    }
    
    @Test
    void testDeactivateUser_UserNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> userService.deactivateUser(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found with ID: 1");
        
        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void testExistsByEmail_Success() {
        // Given
        when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(true);
        
        // When
        boolean exists = userService.existsByEmail("john.doe@example.com");
        
        // Then
        assertThat(exists).isTrue();
        verify(userRepository).existsByEmail("john.doe@example.com");
    }
}
