package com.sprints.room_booking_system.repository;

import com.sprints.room_booking_system.model.User;
import com.sprints.room_booking_system.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Find users by role
     */
    List<User> findByRole(UserRole role);
    
    /**
     * Find users by department
     */
    List<User> findByDepartmentId(Long departmentId);
    
    /**
     * Find active users
     */
    @Query("SELECT u FROM User u WHERE u.isActive = true")
    List<User> findActiveUsers();
    
    /**
     * Find users by role and department
     */
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.department.id = :departmentId")
    List<User> findByRoleAndDepartment(@Param("role") UserRole role, @Param("departmentId") Long departmentId);
    
    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);
}
