package com.sprints.room_booking_system.repository;

import com.sprints.room_booking_system.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class BookingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private RoomRepository roomRepository;

    private User user;
    private Department department;
    private Building building;
    private Room room;
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

        // Create room
        room = Room.builder()
                .name("Room 101")
                .capacity(30)
                .floorNumber(1)
                .building(building)
                .isActive(true)
                .build();
        room = entityManager.persistAndFlush(room);

        // Create an existing approved booking
        existingBooking = Booking.builder()
                .startTime(LocalDateTime.now().plusHours(1))
                .endTime(LocalDateTime.now().plusHours(2))
                .status(BookingStatus.APPROVED)
                .purpose("Lecture")
                .user(user)
                .room(room)
                .build();
        existingBooking = entityManager.persistAndFlush(existingBooking);

        entityManager.clear();
    }

    @Test
    void testFindByUserId() {
        List<Booking> bookings = bookingRepository.findByUserId(user.getId());

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getPurpose()).isEqualTo("Lecture");
        assertThat(bookings.get(0).getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void testFindByRoomId() {
        List<Booking> bookings = bookingRepository.findByRoomId(room.getId());

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getRoom().getName()).isEqualTo("Room 101");
    }

    @Test
    void testFindByStatus() {
        List<Booking> bookings = bookingRepository.findByStatus(BookingStatus.APPROVED);

        assertThat(bookings).hasSize(1);
        assertThat(bookings).extracting("status")
                .allMatch(status -> status == BookingStatus.APPROVED);
    }

    @Test
    void testFindByUserIdAndStatus() {
        List<Booking> bookings = bookingRepository.findByUserIdAndStatus(user.getId(), BookingStatus.APPROVED);

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getUser().getId()).isEqualTo(user.getId());
        assertThat(bookings.get(0).getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void testFindByRoomIdAndStatus() {
        List<Booking> bookings = bookingRepository.findByRoomIdAndStatus(room.getId(), BookingStatus.APPROVED);

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getRoom().getId()).isEqualTo(room.getId());
        assertThat(bookings.get(0).getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void testFindByDateRange() {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);

        List<Booking> bookings = bookingRepository.findByDateRange(startDate, endDate);

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getStartTime()).isAfter(startDate);
        assertThat(bookings.get(0).getStartTime()).isBefore(endDate);
    }

    @Test
    void testFindByRoomAndDateRange() {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);

        List<Booking> bookings = bookingRepository.findByRoomAndDateRange(room.getId(), startDate, endDate);

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getRoom().getId()).isEqualTo(room.getId());
    }

    @Test
    void testHasOverlappingBookings() {
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = LocalDateTime.now().plusHours(2);

        boolean hasOverlap = bookingRepository.hasOverlappingBookings(room.getId(), startTime, endTime);

        assertThat(hasOverlap).isTrue();
    }

    @Test
    void testHasOverlappingBookingsNoConflict() {
        LocalDateTime startTime = LocalDateTime.now().plusHours(3);
        LocalDateTime endTime = LocalDateTime.now().plusHours(4);

        boolean hasOverlap = bookingRepository.hasOverlappingBookings(room.getId(), startTime, endTime);

        assertThat(hasOverlap).isFalse();
    }

    @Test
    void testHasOverlappingBookingsExcludeId() {
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = LocalDateTime.now().plusHours(2);

        boolean hasOverlap = bookingRepository.hasOverlappingBookings(room.getId(), startTime, endTime, existingBooking.getId());

        assertThat(hasOverlap).isFalse();
    }

    @Test
    @Transactional
    void testUpdateBookingStatus() {
        int updatedRows = bookingRepository.updateBookingStatus(existingBooking.getId(), BookingStatus.CANCELLED);

        assertThat(updatedRows).isEqualTo(1);

        // Verify the status was updated
        entityManager.clear();
        Booking updatedBooking = entityManager.find(Booking.class, existingBooking.getId());
        assertThat(updatedBooking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
    }

    @Test
    void testFindActiveBookings() {
        List<Booking> activeBookings = bookingRepository.findActiveBookings();

        assertThat(activeBookings).hasSize(1);
        assertThat(activeBookings).extracting("status")
                .allMatch(status -> status == BookingStatus.APPROVED || status == BookingStatus.PENDING);
    }

    @Test
    void testFindByRoomAndTimeRange() {
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = LocalDateTime.now().plusHours(2);

        List<Booking> bookings = bookingRepository.findByRoomAndTimeRange(room.getId(), startTime, endTime);

        // The existing booking is created with startTime = now + 1 hour, endTime = now + 2 hours
        // The query looks for bookings that are completely within the range [startTime, endTime]
        // Since the existing booking spans the entire range, it should be found
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getRoom().getId()).isEqualTo(room.getId());

        // Test with a wider range that should also find the booking
        LocalDateTime widerStartTime = LocalDateTime.now();
        LocalDateTime widerEndTime = LocalDateTime.now().plusHours(3);
        List<Booking> widerBookings = bookingRepository.findByRoomAndTimeRange(room.getId(), widerStartTime, widerEndTime);
        assertThat(widerBookings).hasSize(1);
    }
}
