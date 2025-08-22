package com.sprints.room_booking_system.service.impl;

import com.sprints.room_booking_system.dto.RoomDto;
import com.sprints.room_booking_system.model.Room;
import com.sprints.room_booking_system.model.RoomFeature;
import com.sprints.room_booking_system.repository.BuildingRepository;
import com.sprints.room_booking_system.repository.RoomFeatureRepository;
import com.sprints.room_booking_system.repository.RoomRepository;
import com.sprints.room_booking_system.repository.BookingRepository;
import com.sprints.room_booking_system.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomServiceImpl implements RoomService {
    
    private final RoomRepository roomRepository;
    private final BuildingRepository buildingRepository;
    private final RoomFeatureRepository roomFeatureRepository;
    private final BookingRepository bookingRepository;
    
    @Override
    public Room createRoom(RoomDto roomDto) {
        // Verify building exists
        if (!buildingRepository.existsById(roomDto.getBuildingId())) {
            throw new IllegalArgumentException("Building not found with ID: " + roomDto.getBuildingId());
        }
        
        Room room = Room.builder()
                .name(roomDto.getName())
                .capacity(roomDto.getCapacity())
                .floorNumber(roomDto.getFloorNumber())
                .isActive(true)
                .build();
        
        // Set building
        room.setBuilding(buildingRepository.findById(roomDto.getBuildingId()).orElse(null));
        
        // Set features if provided
        if (roomDto.getFeatureIds() != null && !roomDto.getFeatureIds().isEmpty()) {
            List<RoomFeature> features = roomFeatureRepository.findByIdIn(roomDto.getFeatureIds());
            room.setFeatures(features);
        }
        
        return roomRepository.save(room);
    }
    
    @Override
    public Room updateRoom(Long roomId, RoomDto roomDto) {
        Room existingRoom = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with ID: " + roomId));
        
        // Verify building exists if being changed
        if (!roomDto.getBuildingId().equals(existingRoom.getBuilding().getId()) && 
            !buildingRepository.existsById(roomDto.getBuildingId())) {
            throw new IllegalArgumentException("Building not found with ID: " + roomDto.getBuildingId());
        }
        
        existingRoom.setName(roomDto.getName());
        existingRoom.setCapacity(roomDto.getCapacity());
        existingRoom.setFloorNumber(roomDto.getFloorNumber());
        
        // Update building if changed
        if (!roomDto.getBuildingId().equals(existingRoom.getBuilding().getId())) {
            existingRoom.setBuilding(buildingRepository.findById(roomDto.getBuildingId()).orElse(null));
        }
        
        // Update features if provided
        if (roomDto.getFeatureIds() != null) {
            List<RoomFeature> features = roomFeatureRepository.findByIdIn(roomDto.getFeatureIds());
            existingRoom.setFeatures(features);
        }
        
        return roomRepository.save(existingRoom);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Room> findById(Long roomId) {
        return roomRepository.findById(roomId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Room> findByName(String name) {
        return roomRepository.findByName(name);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Room> findAllRooms() {
        return roomRepository.findByIsActiveTrue();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Room> findRoomsByBuilding(Long buildingId) {
        return roomRepository.findByBuildingId(buildingId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Room> findRoomsByCapacity(int minCapacity) {
        return roomRepository.findByCapacityGreaterThanEqual(minCapacity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Room> findAvailableRooms(LocalDateTime startTime, LocalDateTime endTime) {
        return roomRepository.findAvailableRooms(startTime, endTime);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Room> findAvailableRoomsWithFeatures(LocalDateTime startTime, LocalDateTime endTime, List<Long> featureIds) {
        if (featureIds == null || featureIds.isEmpty()) {
            return findAvailableRooms(startTime, endTime);
        }
        
        return roomRepository.findAvailableRoomsWithFeatures(startTime, endTime, featureIds);
    }
    
    @Override
    public void deactivateRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with ID: " + roomId));
        
        room.setIsActive(false);
        roomRepository.save(room);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isRoomAvailable(Long roomId, LocalDateTime startTime, LocalDateTime endTime) {
        // Check if room exists and is active
        Optional<Room> roomOpt = roomRepository.findById(roomId);
        if (roomOpt.isEmpty() || !roomOpt.get().getIsActive()) {
            return false;
        }
        
        // Check for overlapping bookings using the repository's overlap detection
        return !bookingRepository.hasOverlappingBookings(roomId, startTime, endTime);
    }
}
