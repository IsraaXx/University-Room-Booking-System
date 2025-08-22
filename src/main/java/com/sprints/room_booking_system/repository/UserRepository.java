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
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.isActive = true")
    List<User> findActiveUsers();

    @Query("SELECT u FROM User u WHERE u.role = :role AND u.department.id = :departmentId")
    List<User> findByRoleAndDepartment(@Param("role") UserRole role, @Param("departmentId") Long departmentId);
    
    List<User> findByRole(UserRole role);
    List<User> findByDepartmentId(Long departmentId);
    List<User> findByIsActiveTrue();
}
