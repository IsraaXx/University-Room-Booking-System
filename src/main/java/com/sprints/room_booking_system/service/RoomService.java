package com.sprints.room_booking_system.service;

import com.sprints.room_booking_system.dto.RoomDto;
import com.sprints.room_booking_system.model.Room;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RoomService {
    
    /**
     * Create a new room
     */
    Room createRoom(RoomDto roomDto);
    
    /**
     * Update an existing room
     */
    Room updateRoom(Long roomId, RoomDto roomDto);
    
    /**
     * Find room by ID
     */
    Optional<Room> findById(Long roomId);
    
    /**
     * Find room by name
     */
    Optional<Room> findByName(String name);
    
    /**
     * Find all rooms
     */
    List<Room> findAllRooms();
    
    /**
     * Find rooms by building
     */
    List<Room> findRoomsByBuilding(Long buildingId);
    
    /**
     * Find rooms by capacity
     */
    List<Room> findRoomsByCapacity(int minCapacity);
    
    /**
     * Find available rooms for a time period
     */
    List<Room> findAvailableRooms(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * Find available rooms with specific features
     */
    List<Room> findAvailableRoomsWithFeatures(LocalDateTime startTime, LocalDateTime endTime, List<Long> featureIds);
    
    /**
     * Deactivate room
     */
    void deactivateRoom(Long roomId);
    
    /**
     * Check if room is available for a time period
     */
    boolean isRoomAvailable(Long roomId, LocalDateTime startTime, LocalDateTime endTime);
}
