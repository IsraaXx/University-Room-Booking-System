package com.sprints.room_booking_system.repository;

import com.sprints.room_booking_system.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    
    /**
     * Find department by name
     */
    Optional<Department> findByName(String name);
    
    /**
     * Find departments by name containing
     */
    List<Department> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find active departments
     */
    @Query("SELECT d FROM Department d WHERE d.name IS NOT NULL")
    List<Department> findActiveDepartments();
}
