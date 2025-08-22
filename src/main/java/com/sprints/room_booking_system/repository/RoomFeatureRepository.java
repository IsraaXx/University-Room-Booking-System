package com.sprints.room_booking_system.repository;

import com.sprints.room_booking_system.model.RoomFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomFeatureRepository extends JpaRepository<RoomFeature, Long> {
    
    /**
     * Find feature by name
     */
    Optional<RoomFeature> findByName(String name);
    
    /**
     * Find features by name containing
     */
    List<RoomFeature> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find features by room
     */
    @Query("SELECT f FROM RoomFeature f JOIN f.rooms r WHERE r.id = :roomId")
    List<RoomFeature> findByRoomId(@Param("roomId") Long roomId);
    
    /**
     * Find features by multiple IDs
     */
    List<RoomFeature> findByIdIn(List<Long> featureIds);
    
    /**
     * Check if feature exists by name
     */
    boolean existsByName(String name);
}
