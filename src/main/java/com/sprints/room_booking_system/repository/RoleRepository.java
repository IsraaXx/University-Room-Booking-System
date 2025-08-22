package com.sprints.room_booking_system.repository;

import com.sprints.room_booking_system.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    /**
     * Find role by name
     */
    Optional<Role> findByName(String name);
    
    /**
     * Find all active roles
     */
    @Query("SELECT r FROM Role r WHERE r.name IN ('STUDENT', 'FACULTY', 'ADMIN')")
    List<Role> findActiveRoles();
    
    /**
     * Check if role exists by name
     */
    boolean existsByName(String name);
}
