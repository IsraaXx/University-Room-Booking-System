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
    RoomDto createRoom(RoomDto roomDto);
    
    /**
     * Update an existing room
     */
    RoomDto updateRoom(Long roomId, RoomDto roomDto);
    
    /**
     * Find room by ID
     */
    Optional<RoomDto> findById(Long roomId);
    
    /**
     * Find room by name
     */
    Optional<RoomDto> findByName(String name);
    
    /**
     * Find all rooms
     */
    List<RoomDto> findAllRooms();
    
    /**
     * Find rooms by building
     */
    List<RoomDto> findRoomsByBuilding(Long buildingId);
    
    /**
     * Find rooms by capacity
     */
    List<RoomDto> findRoomsByCapacity(int minCapacity);
    
    /**
     * Find available rooms for a time period
     */
    List<RoomDto> findAvailableRooms(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * Find available rooms with specific features
     */
    List<RoomDto> findAvailableRoomsWithFeatures(LocalDateTime startTime, LocalDateTime endTime, List<Long> featureIds);
    
    /**
     * Deactivate room
     */
    void deactivateRoom(Long roomId);
    
    /**
     * Check if room is available for a time period
     */
    boolean isRoomAvailable(Long roomId, LocalDateTime startTime, LocalDateTime endTime);
}
