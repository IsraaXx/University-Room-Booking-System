package com.sprints.room_booking_system.repository;

import com.sprints.room_booking_system.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class RoomRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private RoomFeatureRepository roomFeatureRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private Building building;
    private Room room1, room2, room3;
    private RoomFeature feature1, feature2;
    private User user;
    private Department department;
    private Booking existingBooking;

    @BeforeEach
    void setUp() {
        // Create department
        department = Department.builder()
                .name("Computer Science")
                .build();
        department = entityManager.persistAndFlush(department);

        // Create user
        user = User.builder()
                .name("John Doe")
                .email("john.doe@university.edu")
                .password("password123")
                .role(UserRole.FACULTY)
                .department(department)
                .isActive(true)
                .build();
        user = entityManager.persistAndFlush(user);

        // Create building
        building = Building.builder()
                .name("Engineering Building")
                .location("123 Engineering St")
                .build();
        building = entityManager.persistAndFlush(building);

        // Create room features
        feature1 = RoomFeature.builder()
                .name("Projector")
                .build();
        feature1 = entityManager.persistAndFlush(feature1);

        feature2 = RoomFeature.builder()
                .name("Whiteboard")
                .build();
        feature2 = entityManager.persistAndFlush(feature2);

        // Create rooms
        room1 = Room.builder()
                .name("Room 101")
                .capacity(30)
                .floorNumber(1)
                .building(building)
                .features(Arrays.asList(feature1, feature2))
                .isActive(true)
                .build();
        room1 = entityManager.persistAndFlush(room1);

        room2 = Room.builder()
                .name("Room 102")
                .capacity(20)
                .floorNumber(1)
                .building(building)
                .features(Arrays.asList(feature1))
                .isActive(true)
                .build();
        room2 = entityManager.persistAndFlush(room2);

        room3 = Room.builder()
                .name("Room 103")
                .capacity(50)
                .floorNumber(2)
                .building(building)
                .features(Arrays.asList(feature2))
                .isActive(true)
                .build();
        room3 = entityManager.persistAndFlush(room3);

        // Create an existing booking for room1
        existingBooking = Booking.builder()
                .startTime(LocalDateTime.now().plusHours(1))
                .endTime(LocalDateTime.now().plusHours(2))
                .status(BookingStatus.APPROVED)
                .purpose("Lecture")
                .user(user)
                .room(room1)
                .build();
        existingBooking = entityManager.persistAndFlush(existingBooking);

        entityManager.clear();
    }

    @Test
    void testFindByBuildingId() {
        List<Room> rooms = roomRepository.findByBuildingId(building.getId());
        
        assertThat(rooms).hasSize(3);
        assertThat(rooms).extracting("name")
                .containsExactlyInAnyOrder("Room 101", "Room 102", "Room 103");
    }

    @Test
    void testFindByCapacityGreaterThanEqual() {
        List<Room> rooms = roomRepository.findByCapacityGreaterThanEqual(25);
        
        assertThat(rooms).hasSize(2);
        assertThat(rooms).extracting("capacity")
                .allMatch(capacity -> (Integer) capacity >= 25);
    }

    @Test
    void testFindByFloorNumber() {
        List<Room> rooms = roomRepository.findByFloorNumber(1);
        
        assertThat(rooms).hasSize(2);
        assertThat(rooms).extracting("floorNumber")
                .allMatch(floor -> (Integer) floor == 1);
    }

    @Test
    void testFindByIsActiveTrue() {
        List<Room> rooms = roomRepository.findByIsActiveTrue();
        
        assertThat(rooms).hasSize(3);
        assertThat(rooms).extracting("isActive")
                .allMatch(active -> (Boolean) active);
    }

    @Test
    void testFindByBuildingIdAndCapacityGreaterThanEqual() {
        List<Room> rooms = roomRepository.findByBuildingIdAndCapacityGreaterThanEqual(building.getId(), 25);
        
        assertThat(rooms).hasSize(2);
        assertThat(rooms).extracting("building.id")
                .allMatch(buildingId -> buildingId.equals(building.getId()));
        assertThat(rooms).extracting("capacity")
                .allMatch(capacity -> (Integer) capacity >= 25);
    }

    @Test
    void testFindAvailableRooms() {
        LocalDateTime startTime = LocalDateTime.now().plusHours(3);
        LocalDateTime endTime = LocalDateTime.now().plusHours(4);
        
        List<Room> availableRooms = roomRepository.findAvailableRooms(startTime, endTime);
        
        // Should return all rooms since the time doesn't conflict with existing booking
        assertThat(availableRooms).hasSize(3);
        assertThat(availableRooms).extracting("name")
                .containsExactlyInAnyOrder("Room 101", "Room 102", "Room 103");
    }

    @Test
    void testFindAvailableRoomsWithConflict() {
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = LocalDateTime.now().plusHours(2);
        
        List<Room> availableRooms = roomRepository.findAvailableRooms(startTime, endTime);
        
        // Should not return room1 since it has a conflicting booking
        assertThat(availableRooms).hasSize(2);
        assertThat(availableRooms).extracting("name")
                .containsExactlyInAnyOrder("Room 102", "Room 103");
        assertThat(availableRooms).extracting("name")
                .doesNotContain("Room 101");
    }

    @Test
    void testFindAvailableRoomsWithFeatures() {
        LocalDateTime startTime = LocalDateTime.now().plusHours(3);
        LocalDateTime endTime = LocalDateTime.now().plusHours(4);
        List<Long> featureIds = Arrays.asList(feature1.getId());
        
        List<Room> availableRooms = roomRepository.findAvailableRoomsWithFeatures(startTime, endTime, featureIds);
        
        // Should return rooms with feature1 (Room 101 and Room 102)
        assertThat(availableRooms).hasSize(2);
        assertThat(availableRooms).extracting("name")
                .containsExactlyInAnyOrder("Room 101", "Room 102");
    }

    @Test
    void testFindRoomsByBuildingAndFeatures() {
        List<Long> featureIds = Arrays.asList(feature1.getId(), feature2.getId());
        
        List<Room> rooms = roomRepository.findRoomsByBuildingAndFeatures(building.getId(), featureIds, (long) featureIds.size());
        
        // Should return rooms with both features (only Room 101 has both features)
        assertThat(rooms).hasSize(1);
        assertThat(rooms.get(0).getName()).isEqualTo("Room 101");
        
        // Test with only one feature
        List<Long> singleFeatureIds = Arrays.asList(feature1.getId());
        List<Room> roomsWithFeature1 = roomRepository.findRoomsByBuildingAndFeatures(building.getId(), singleFeatureIds, (long) singleFeatureIds.size());
        
        // Should return rooms with feature1 (Room 101 and Room 102)
        assertThat(roomsWithFeature1).hasSize(2);
        assertThat(roomsWithFeature1).extracting("name")
                .containsExactlyInAnyOrder("Room 101", "Room 102");
    }

    @Test
    void testFindByName() {
        Optional<Room> room = roomRepository.findByName("Room 101");
        
        assertThat(room).isPresent();
        assertThat(room.get().getCapacity()).isEqualTo(30);
        assertThat(room.get().getBuilding().getName()).isEqualTo("Engineering Building");
    }
}
