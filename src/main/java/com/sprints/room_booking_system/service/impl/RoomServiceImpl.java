package com.sprints.room_booking_system.service.impl;

import com.sprints.room_booking_system.dto.RoomDto;
import com.sprints.room_booking_system.model.Building;
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
    public RoomDto createRoom(RoomDto roomDto) {
        // Find Building
        Building building = buildingRepository.findById(roomDto.getBuildingId())
                .orElseThrow(() -> new IllegalArgumentException("Building not found with ID: " + roomDto.getBuildingId()));

        // Set features if provided
        List<RoomFeature> features = roomDto.getFeatureIds() != null && !roomDto.getFeatureIds().isEmpty()
                ? roomFeatureRepository.findByIdIn(roomDto.getFeatureIds())
                : null;


        Room room = Room.builder()
                .name(roomDto.getName())
                .capacity(roomDto.getCapacity())
                .floorNumber(roomDto.getFloorNumber())
                .isActive(true)
                .building(building)
                .features(features)
                .build();

        Room savedRoom = roomRepository.save(room);
        return toDto(savedRoom);
    }
    
    @Override
    public RoomDto updateRoom(Long roomId, RoomDto roomDto) {
        Room existingRoom = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with ID: " + roomId));

        // Find Building if it's being changed
        Building newBuilding = existingRoom.getBuilding();
        if (!roomDto.getBuildingId().equals(existingRoom.getBuilding().getId())) {
            newBuilding = buildingRepository.findById(roomDto.getBuildingId())
                    .orElseThrow(() -> new IllegalArgumentException("Building not found with ID: " + roomDto.getBuildingId()));
        }

        existingRoom.setName(roomDto.getName());
        existingRoom.setCapacity(roomDto.getCapacity());
        existingRoom.setFloorNumber(roomDto.getFloorNumber());
        existingRoom.setBuilding(newBuilding);

        // Update features if provided
        if (roomDto.getFeatureIds() != null) {
            List<RoomFeature> features = roomFeatureRepository.findByIdIn(roomDto.getFeatureIds());
            existingRoom.setFeatures(features);
        }

        Room updatedRoom = roomRepository.save(existingRoom);
        return toDto(updatedRoom);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<RoomDto> findById(Long roomId) {
        return roomRepository.findById(roomId).map(this::toDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<RoomDto> findByName(String name) {
        return roomRepository.findByName(name).map(this::toDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RoomDto> findAllRooms() {
        return roomRepository.findByIsActiveTrue().stream().map(this::toDto).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RoomDto> findRoomsByBuilding(Long buildingId) {
        return roomRepository.findByBuildingId(buildingId).stream().map(this::toDto).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RoomDto> findRoomsByCapacity(int minCapacity) {
        return roomRepository.findByCapacityGreaterThanEqual(minCapacity).stream().map(this::toDto).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RoomDto> findAvailableRooms(LocalDateTime startTime, LocalDateTime endTime) {
        return roomRepository.findAvailableRooms(startTime, endTime).stream().map(this::toDto).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RoomDto> findAvailableRoomsWithFeatures(LocalDateTime startTime, LocalDateTime endTime, List<Long> featureIds) {
        if (featureIds == null || featureIds.isEmpty()) {
            return findAvailableRooms(startTime, endTime);
        }

        return roomRepository.findAvailableRoomsWithFeatures(startTime, endTime, featureIds).stream().map(this::toDto).collect(Collectors.toList());
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

    private RoomDto toDto(Room room) {
        return RoomDto.builder()
                .name(room.getName())
                .capacity(room.getCapacity())
                .floorNumber(room.getFloorNumber())
                .isActive(room.getIsActive())
                .buildingId(room.getBuilding().getId())
                .featureIds(room.getFeatures().stream().map(RoomFeature::getId).collect(Collectors.toList()))
                .build();
    }
}
