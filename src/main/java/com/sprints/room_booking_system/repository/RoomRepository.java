package com.sprints.room_booking_system.repository;

import com.sprints.room_booking_system.model.Room;
import com.sprints.room_booking_system.model.RoomFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    
    /**
     * Find room by name
     */
    Optional<Room> findByName(String name);
    
    /**
     * Find rooms by building
     */
    List<Room> findByBuildingId(Long buildingId);
    
    /**
     * Find rooms by capacity
     */
    List<Room> findByCapacityGreaterThanEqual(int minCapacity);
    
    /**
     * Find rooms by floor number
     */
    List<Room> findByFloorNumber(int floorNumber);
    
    /**
     * Find active rooms
     */
    List<Room> findByIsActiveTrue();
    
    /**
     * Find rooms by building and capacity
     */
    List<Room> findByBuildingIdAndCapacityGreaterThanEqual(Long buildingId, int minCapacity);
    
    /**
     * Find available rooms for a given time range
     * This query finds rooms that don't have conflicting bookings
     */
    @Query("""
        SELECT DISTINCT r FROM Room r 
        WHERE r.isActive = true 
        AND r.id NOT IN (
            SELECT DISTINCT b.room.id 
            FROM Booking b 
            WHERE b.status IN ('PENDING', 'APPROVED')
            AND (
                (b.startTime <= :startTime AND b.endTime > :startTime) OR
                (b.startTime < :endTime AND b.endTime >= :endTime) OR
                (b.startTime >= :startTime AND b.endTime <= :endTime)
            )
        )
        """)
    List<Room> findAvailableRooms(@Param("startTime") LocalDateTime startTime, 
                                   @Param("endTime") LocalDateTime endTime);
    
    /**
     * Find available rooms with specific features for a given time range
     */
    @Query("""
        SELECT DISTINCT r FROM Room r 
        JOIN r.features f 
        WHERE r.isActive = true 
        AND f.id IN :featureIds
        AND r.id NOT IN (
            SELECT DISTINCT b.room.id 
            FROM Booking b 
            WHERE b.status IN ('PENDING', 'APPROVED')
            AND (
                (b.startTime <= :startTime AND b.endTime > :startTime) OR
                (b.startTime < :endTime AND b.endTime >= :endTime) OR
                (b.startTime >= :startTime AND b.endTime <= :endTime)
            )
        )
        """)
    List<Room> findAvailableRoomsWithFeatures(@Param("startTime") LocalDateTime startTime, 
                                              @Param("endTime") LocalDateTime endTime,
                                              @Param("featureIds") List<Long> featureIds);
    
    /**
     * Find rooms by building and features
     * This query finds rooms that have ALL the specified features
     */
    @Query("""
        SELECT DISTINCT r FROM Room r 
        WHERE r.building.id = :buildingId 
        AND r.isActive = true
        AND :featureCount = (
            SELECT COUNT(f) FROM r.features f WHERE f.id IN :featureIds
        )
        """)
    List<Room> findRoomsByBuildingAndFeatures(@Param("buildingId") Long buildingId, 
                                              @Param("featureIds") List<Long> featureIds,
                                              @Param("featureCount") Long featureCount);
}
