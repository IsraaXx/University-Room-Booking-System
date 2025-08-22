package com.sprints.room_booking_system.repository;

import com.sprints.room_booking_system.model.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {
    
    /**
     * Find building by name
     */
    Optional<Building> findByName(String name);
    
    /**
     * Find buildings by location
     */
    List<Building> findByLocation(String location);
    
    /**
     * Find buildings by name containing
     */
    List<Building> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find buildings with rooms
     */
    @Query("SELECT DISTINCT b FROM Building b JOIN b.rooms r WHERE r.isActive = true")
    List<Building> findBuildingsWithActiveRooms();
    
    /**
     * Find buildings by location containing
     */
    List<Building> findByLocationContainingIgnoreCase(String location);
}
