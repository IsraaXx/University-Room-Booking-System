package com.sprints.room_booking_system.service;

import com.sprints.room_booking_system.dto.RoomDto;
import com.sprints.room_booking_system.model.Building;
import com.sprints.room_booking_system.model.Room;
import com.sprints.room_booking_system.model.RoomFeature;
import com.sprints.room_booking_system.repository.BookingRepository;
import com.sprints.room_booking_system.repository.BuildingRepository;
import com.sprints.room_booking_system.repository.RoomFeatureRepository;
import com.sprints.room_booking_system.repository.RoomRepository;
import com.sprints.room_booking_system.service.impl.RoomServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {
    
    @Mock
    private RoomRepository roomRepository;
    
    @Mock
    private BuildingRepository buildingRepository;
    
    @Mock
    private RoomFeatureRepository roomFeatureRepository;
    
    @Mock
    private BookingRepository bookingRepository;
    
    @InjectMocks
    private RoomServiceImpl roomService;
    
    private RoomDto roomDto;
    private Room room;
    private Building building;
    private RoomFeature feature;
    
    @BeforeEach
    void setUp() {
        building = Building.builder()
                .id(1L)
                .name("Engineering Building")
                .location("Main Campus")
                .build();
        
        feature = RoomFeature.builder()
                .id(1L)
                .name("Projector")
                .build();
        
        roomDto = RoomDto.builder()
                .name("Room 101")
                .capacity(25)
                .floorNumber(1)
                .buildingId(1L)
                .featureIds(Arrays.asList(1L))
                .build();
        
        room = Room.builder()
                .id(1L)
                .name("Room 101")
                .capacity(25)
                .floorNumber(1)
                .building(building)
                .features(Arrays.asList(feature))
                .isActive(true)
                .build();
    }
    
    @Test
    void testCreateRoom_Success() {
        // Given
        when(buildingRepository.existsById(1L)).thenReturn(true);
        when(buildingRepository.findById(1L)).thenReturn(Optional.of(building));
        when(roomFeatureRepository.findByIdIn(Arrays.asList(1L))).thenReturn(Arrays.asList(feature));
        when(roomRepository.save(any(Room.class))).thenReturn(room);
        
        // When
        Room createdRoom = roomService.createRoom(roomDto);
        
        // Then
        assertThat(createdRoom).isNotNull();
        assertThat(createdRoom.getName()).isEqualTo("Room 101");
        assertThat(createdRoom.getCapacity()).isEqualTo(25);
        assertThat(createdRoom.getFloorNumber()).isEqualTo(1);
        assertThat(createdRoom.getBuilding()).isEqualTo(building);
        assertThat(createdRoom.getFeatures()).hasSize(1);
        assertThat(createdRoom.getIsActive()).isTrue();
        
        verify(buildingRepository).existsById(1L);
        verify(buildingRepository).findById(1L);
        verify(roomFeatureRepository).findByIdIn(Arrays.asList(1L));
        verify(roomRepository).save(any(Room.class));
    }
    
    @Test
    void testCreateRoom_BuildingNotFound() {
        // Given
        when(buildingRepository.existsById(1L)).thenReturn(false);
        
        // When & Then
        assertThatThrownBy(() -> roomService.createRoom(roomDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Building not found with ID: 1");
        
        verify(buildingRepository).existsById(1L);
        verify(roomRepository, never()).save(any(Room.class));
    }
    
    @Test
    void testCreateRoom_NoFeatures() {
        // Given
        RoomDto roomDtoNoFeatures = RoomDto.builder()
                .name("Room 102")
                .capacity(30)
                .floorNumber(1)
                .buildingId(1L)
                .build();
        
        when(buildingRepository.existsById(1L)).thenReturn(true);
        when(buildingRepository.findById(1L)).thenReturn(Optional.of(building));
        when(roomRepository.save(any(Room.class))).thenReturn(room);
        
        // When
        Room createdRoom = roomService.createRoom(roomDtoNoFeatures);
        
        // Then
        assertThat(createdRoom).isNotNull();
        verify(roomFeatureRepository, never()).findByIdIn(any());
    }
    
    @Test
    void testUpdateRoom_Success() {
        // Given
        Room existingRoom = Room.builder()
                .id(1L)
                .name("Room 101")
                .capacity(25)
                .floorNumber(1)
                .building(building)
                .features(Arrays.asList(feature))
                .isActive(true)
                .build();
        
        RoomDto updateDto = RoomDto.builder()
                .name("Room 101 Updated")
                .capacity(30)
                .floorNumber(2)
                .buildingId(1L)
                .featureIds(Arrays.asList(1L, 2L))
                .build();
        
        RoomFeature feature2 = RoomFeature.builder().id(2L).name("Whiteboard").build();
        
        when(roomRepository.findById(1L)).thenReturn(Optional.of(existingRoom));
        when(roomFeatureRepository.findByIdIn(Arrays.asList(1L, 2L))).thenReturn(Arrays.asList(feature, feature2));
        when(roomRepository.save(any(Room.class))).thenReturn(existingRoom);
        
        // When
        Room updatedRoom = roomService.updateRoom(1L, updateDto);
        
        // Then
        assertThat(updatedRoom).isNotNull();
        assertThat(updatedRoom.getName()).isEqualTo("Room 101 Updated");
        assertThat(updatedRoom.getCapacity()).isEqualTo(30);
        assertThat(updatedRoom.getFloorNumber()).isEqualTo(2);
        
        verify(roomRepository).findById(1L);
        verify(roomRepository).save(existingRoom);
    }
    
    @Test
    void testUpdateRoom_RoomNotFound() {
        // Given
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> roomService.updateRoom(1L, roomDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Room not found with ID: 1");
        
        verify(roomRepository).findById(1L);
        verify(roomRepository, never()).save(any(Room.class));
    }
    
    @Test
    void testUpdateRoom_BuildingNotFound() {
        // Given
        Room existingRoom = Room.builder()
                .id(1L)
                .name("Room 101")
                .capacity(25)
                .floorNumber(1)
                .building(building)
                .features(Arrays.asList(feature))
                .isActive(true)
                .build();
        
        RoomDto updateDto = RoomDto.builder()
                .name("Room 101 Updated")
                .capacity(30)
                .floorNumber(2)
                .buildingId(2L) // Different building
                .build();
        
        when(roomRepository.findById(1L)).thenReturn(Optional.of(existingRoom));
        when(buildingRepository.existsById(2L)).thenReturn(false);
        
        // When & Then
        assertThatThrownBy(() -> roomService.updateRoom(1L, updateDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Building not found with ID: 2");
        
        verify(roomRepository).findById(1L);
        verify(buildingRepository).existsById(2L);
        verify(roomRepository, never()).save(any(Room.class));
    }
    
    @Test
    void testFindById_Success() {
        // Given
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        
        // When
        Optional<Room> foundRoom = roomService.findById(1L);
        
        // Then
        assertThat(foundRoom).isPresent();
        assertThat(foundRoom.get().getName()).isEqualTo("Room 101");
        verify(roomRepository).findById(1L);
    }
    
    @Test
    void testFindById_NotFound() {
        // Given
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When
        Optional<Room> foundRoom = roomService.findById(1L);
        
        // Then
        assertThat(foundRoom).isEmpty();
        verify(roomRepository).findById(1L);
    }
    
    @Test
    void testFindByName_Success() {
        // Given
        when(roomRepository.findByName("Room 101")).thenReturn(Optional.of(room));
        
        // When
        Optional<Room> foundRoom = roomService.findByName("Room 101");
        
        // Then
        assertThat(foundRoom).isPresent();
        assertThat(foundRoom.get().getName()).isEqualTo("Room 101");
        verify(roomRepository).findByName("Room 101");
    }
    
    @Test
    void testFindAllRooms_Success() {
        // Given
        List<Room> rooms = Arrays.asList(room);
        when(roomRepository.findByIsActiveTrue()).thenReturn(rooms);
        
        // When
        List<Room> allRooms = roomService.findAllRooms();
        
        // Then
        assertThat(allRooms).hasSize(1);
        assertThat(allRooms.get(0).getName()).isEqualTo("Room 101");
        verify(roomRepository).findByIsActiveTrue();
    }
    
    @Test
    void testFindRoomsByBuilding_Success() {
        // Given
        List<Room> buildingRooms = Arrays.asList(room);
        when(roomRepository.findByBuildingId(1L)).thenReturn(buildingRooms);
        
        // When
        List<Room> roomsByBuilding = roomService.findRoomsByBuilding(1L);
        
        // Then
        assertThat(roomsByBuilding).hasSize(1);
        verify(roomRepository).findByBuildingId(1L);
    }
    
    @Test
    void testFindRoomsByCapacity_Success() {
        // Given
        List<Room> capacityRooms = Arrays.asList(room);
        when(roomRepository.findByCapacityGreaterThanEqual(20)).thenReturn(capacityRooms);
        
        // When
        List<Room> roomsByCapacity = roomService.findRoomsByCapacity(20);
        
        // Then
        assertThat(roomsByCapacity).hasSize(1);
        verify(roomRepository).findByCapacityGreaterThanEqual(20);
    }
    
    @Test
    void testFindAvailableRooms_Success() {
        // Given
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = LocalDateTime.now().plusHours(2);
        List<Room> availableRooms = Arrays.asList(room);
        
        when(roomRepository.findAvailableRooms(startTime, endTime)).thenReturn(availableRooms);
        
        // When
        List<Room> available = roomService.findAvailableRooms(startTime, endTime);
        
        // Then
        assertThat(available).hasSize(1);
        verify(roomRepository).findAvailableRooms(startTime, endTime);
    }
    
    @Test
    void testFindAvailableRoomsWithFeatures_Success() {
        // Given
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = LocalDateTime.now().plusHours(2);
        List<Long> featureIds = Arrays.asList(1L);
        List<Room> availableRooms = Arrays.asList(room);
        
        when(roomRepository.findAvailableRoomsWithFeatures(startTime, endTime, featureIds)).thenReturn(availableRooms);
        
        // When
        List<Room> available = roomService.findAvailableRoomsWithFeatures(startTime, endTime, featureIds);
        
        // Then
        assertThat(available).hasSize(1);
        verify(roomRepository).findAvailableRoomsWithFeatures(startTime, endTime, featureIds);
    }
    
    @Test
    void testFindAvailableRoomsWithFeatures_NoFeatures() {
        // Given
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = LocalDateTime.now().plusHours(2);
        List<Room> availableRooms = Arrays.asList(room);
        
        when(roomRepository.findAvailableRooms(startTime, endTime)).thenReturn(availableRooms);
        
        // When
        List<Room> available = roomService.findAvailableRoomsWithFeatures(startTime, endTime, null);
        
        // Then
        assertThat(available).hasSize(1);
        verify(roomRepository).findAvailableRooms(startTime, endTime);
        verify(roomRepository, never()).findAvailableRoomsWithFeatures(any(), any(), any());
    }
    
    @Test
    void testDeactivateRoom_Success() {
        // Given
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(roomRepository.save(any(Room.class))).thenReturn(room);
        
        // When
        roomService.deactivateRoom(1L);
        
        // Then
        assertThat(room.getIsActive()).isFalse();
        verify(roomRepository).findById(1L);
        verify(roomRepository).save(room);
    }
    
    @Test
    void testDeactivateRoom_RoomNotFound() {
        // Given
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> roomService.deactivateRoom(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Room not found with ID: 1");
        
        verify(roomRepository).findById(1L);
        verify(roomRepository, never()).save(any(Room.class));
    }
    
    @Test
    void testIsRoomAvailable_Success() {
        // Given
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = LocalDateTime.now().plusHours(2);
        
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(bookingRepository.hasOverlappingBookings(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(false);
        
        // When
        boolean isAvailable = roomService.isRoomAvailable(1L, startTime, endTime);
        
        // Then
        assertThat(isAvailable).isTrue();
        verify(roomRepository).findById(1L);
        verify(bookingRepository).hasOverlappingBookings(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class));
    }
    
    @Test
    void testIsRoomAvailable_RoomNotFound() {
        // Given
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = LocalDateTime.now().plusHours(2);
        
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When
        boolean isAvailable = roomService.isRoomAvailable(1L, startTime, endTime);
        
        // Then
        assertThat(isAvailable).isFalse();
        verify(roomRepository).findById(1L);
        verify(bookingRepository, never()).hasOverlappingBookings(any(), any(), any());
    }
    
    @Test
    void testIsRoomAvailable_RoomInactive() {
        // Given
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = LocalDateTime.now().plusHours(2);
        
        Room inactiveRoom = Room.builder()
                .id(1L)
                .name("Room 101")
                .capacity(25)
                .floorNumber(1)
                .building(building)
                .isActive(false)
                .build();
        
        when(roomRepository.findById(1L)).thenReturn(Optional.of(inactiveRoom));
        
        // When
        boolean isAvailable = roomService.isRoomAvailable(1L, startTime, endTime);
        
        // Then
        assertThat(isAvailable).isFalse();
        verify(roomRepository).findById(1L);
        verify(bookingRepository, never()).hasOverlappingBookings(any(), any(), any());
    }
}
