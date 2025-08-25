package com.sprints.room_booking_system.service;

import com.sprints.room_booking_system.dto.BookingDto;
import com.sprints.room_booking_system.exception.BookingConflictException;
import com.sprints.room_booking_system.exception.InvalidBookingDateException;
import com.sprints.room_booking_system.exception.UnauthorizedOperationException;
import com.sprints.room_booking_system.model.*;
import com.sprints.room_booking_system.repository.BookingHistoryRepository;
import com.sprints.room_booking_system.repository.BookingRepository;
import com.sprints.room_booking_system.repository.HolidayRepository;
import com.sprints.room_booking_system.repository.RoomRepository;
import com.sprints.room_booking_system.repository.UserRepository;
import com.sprints.room_booking_system.service.impl.BookingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
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
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private HolidayRepository holidayRepository;

    @Mock
    private BookingHistoryRepository bookingHistoryRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private BookingDto bookingDto;
    private User user;
    private Room room;
    private Building building;
    private Department department;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @BeforeEach
    void setUp() {
        department = Department.builder()
                .id(1L)
                .name("Computer Science")
                .build();

        user = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .password("password123")
                .role(UserRole.STUDENT)
                .department(department)
                .isActive(true)
                .build();

        building = Building.builder()
                .id(1L)
                .name("Engineering Building")
                .location("Main Campus")
                .build();

        room = Room.builder()
                .id(1L)
                .name("Room 101")
                .capacity(25)
                .floorNumber(1)
                .building(building)
                .isActive(true)
                .build();

        startTime = LocalDateTime.now().plusHours(1);
        endTime = LocalDateTime.now().plusHours(2);

        bookingDto = BookingDto.builder()
                .roomId(1L)
                .userId(1L)
                .startTime(startTime)
                .endTime(endTime)
                .purpose("Study Group")
                .build();
    }

    // Create Booking Tests

    @Test
    void testCreateBooking_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(holidayRepository.findByDateRange(any(LocalDate.class), any(LocalDate.class))).thenReturn(Arrays.asList());
        when(bookingRepository.hasOverlappingBookings(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenReturn(createSampleBooking());
        when(bookingHistoryRepository.save(any(BookingHistory.class))).thenReturn(new BookingHistory());

        // When
        BookingDto createdBookingDto = bookingService.createBooking(bookingDto, 1L);

        // Then
        assertThat(createdBookingDto).isNotNull();
        verify(userRepository).findById(1L);
        verify(roomRepository).findById(1L);
        verify(holidayRepository).findByDateRange(any(LocalDate.class), any(LocalDate.class));
        verify(bookingRepository).hasOverlappingBookings(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class));
        verify(bookingRepository).save(any(Booking.class));
        verify(bookingHistoryRepository).save(any(BookingHistory.class));
    }

    @Test
    void testCreateBooking_StartTimeInPast() {
        // Given
        bookingDto.setStartTime(LocalDateTime.now().minusHours(1));

        // When & Then
        assertThatThrownBy(() -> bookingService.createBooking(bookingDto, 1L))
                .isInstanceOf(InvalidBookingDateException.class)
                .hasMessage("Start time cannot be in the past");

        verify(userRepository, never()).findById(any());
        verify(roomRepository, never()).findById(any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void testCreateBooking_EndTimeBeforeStartTime() {
        // Given
        bookingDto.setEndTime(startTime.minusHours(1));

        // When & Then
        assertThatThrownBy(() -> bookingService.createBooking(bookingDto, 1L))
                .isInstanceOf(InvalidBookingDateException.class)
                .hasMessage("End time must be after start time");

        verify(userRepository, never()).findById(any());
        verify(roomRepository, never()).findById(any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void testCreateBooking_EndTimeEqualsStartTime() {
        // Given
        bookingDto.setEndTime(startTime);
        // When & Then
        assertThatThrownBy(() -> bookingService.createBooking(bookingDto, 1L))
                .isInstanceOf(InvalidBookingDateException.class)
                .hasMessage("End time must be after start time");

        verify(userRepository, never()).findById(any());
        verify(roomRepository, never()).findById(any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void testCreateBooking_ExceedsMaxWindow() {
        // Given
        bookingDto.setStartTime(LocalDateTime.now().plusDays(91));
        bookingDto.setEndTime(LocalDateTime.now().plusDays(92)); // Ensure end time is after start time

        // When & Then
        assertThatThrownBy(() -> bookingService.createBooking(bookingDto, 1L))
                .isInstanceOf(InvalidBookingDateException.class)
                .hasMessage("Booking cannot be made more than 90 days in advance");

        verify(userRepository, never()).findById(any());
        verify(roomRepository, never()).findById(any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void testCreateBooking_RoomNotAvailable() {
        // Given
        // Mock the isRoomAvailable method to return false
        when(bookingRepository.hasOverlappingBookings(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> bookingService.createBooking(bookingDto, 1L))
                .isInstanceOf(BookingConflictException.class)
                .hasMessage("Room is not available for the specified time period");

        verify(bookingRepository).hasOverlappingBookings(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class));
        verify(userRepository, never()).findById(any());
        verify(roomRepository, never()).findById(any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void testCreateBooking_HolidayConflict() {
        // Given
        // Mock the holiday repository to return a holiday
        Holiday holiday = Holiday.builder()
                .id(1L)
                .name("New Year")
                .date(LocalDate.now().plusDays(1))
                .description("New Year Holiday")
                .build();

        when(holidayRepository.findByDateRange(any(LocalDate.class), any(LocalDate.class))).thenReturn(Arrays.asList(holiday));

        // When & Then
        assertThatThrownBy(() -> bookingService.createBooking(bookingDto, 1L))
                .isInstanceOf(InvalidBookingDateException.class)
                .hasMessage("Cannot book on holidays: New Year");

        verify(holidayRepository).findByDateRange(any(LocalDate.class), any(LocalDate.class));
        verify(userRepository, never()).findById(any());
        verify(roomRepository, never()).findById(any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void testCreateBooking_UserNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> bookingService.createBooking(bookingDto, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found with ID: 1");

        verify(userRepository).findById(1L);
        verify(roomRepository, never()).findById(any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void testCreateBooking_RoomNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> bookingService.createBooking(bookingDto, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Room not found with ID: 1");

        verify(userRepository).findById(1L);
        verify(roomRepository).findById(1L);
        verify(bookingRepository, never()).save(any());
    }

    // Update Booking Tests

    @Test
    void testUpdateBooking_Success() {
        // Given
        Booking existingBooking = createSampleBooking();
        existingBooking.setStatus(BookingStatus.PENDING);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(existingBooking));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(holidayRepository.findByDateRange(any(LocalDate.class), any(LocalDate.class))).thenReturn(Arrays.asList());
        when(bookingRepository.hasOverlappingBookings(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class), eq(1L))).thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenReturn(existingBooking);
        when(bookingHistoryRepository.save(any(BookingHistory.class))).thenReturn(new BookingHistory());

        // When
        BookingDto updatedBookingDto = bookingService.updateBooking(1L, bookingDto, 1L);

        // Then
        assertThat(updatedBookingDto).isNotNull();
        verify(bookingRepository).findById(1L);
        verify(bookingRepository).save(existingBooking);
        verify(bookingHistoryRepository).save(any(BookingHistory.class));
    }

    @Test
    void testUpdateBooking_UnauthorizedUser() {
        // Given
        Booking existingBooking = createSampleBooking();
        existingBooking.setUser(User.builder().id(2L).build()); // Different user

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(existingBooking));

        // When & Then
        assertThatThrownBy(() -> bookingService.updateBooking(1L, bookingDto, 1L))
                .isInstanceOf(UnauthorizedOperationException.class)
                .hasMessage("User can only update their own bookings");

        verify(bookingRepository).findById(1L);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void testUpdateBooking_NotPendingStatus() {
        // Given
        Booking existingBooking = createSampleBooking();
        existingBooking.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(existingBooking));

        // When & Then
        assertThatThrownBy(() -> bookingService.updateBooking(1L, bookingDto, 1L))
                .isInstanceOf(UnauthorizedOperationException.class)
                .hasMessage("Only PENDING bookings can be updated");

        verify(bookingRepository).findById(1L);
        verify(bookingRepository, never()).save(any());
    }

    // Approve Booking Tests

    @Test
    void testApproveBooking_Success() {
        // Given
        User admin = User.builder()
                .id(2L)
                .name("Admin User")
                .role(UserRole.ADMIN)
                .build();

        Booking pendingBooking = createSampleBooking();
        pendingBooking.setStatus(BookingStatus.PENDING);

        when(userRepository.findById(2L)).thenReturn(Optional.of(admin));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(pendingBooking));
        when(bookingRepository.hasOverlappingBookings(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenReturn(pendingBooking);
        when(bookingHistoryRepository.save(any(BookingHistory.class))).thenReturn(new BookingHistory());

        // When
        BookingDto approvedBooking = bookingService.approveBooking(1L, 2L);

        // Then
        assertThat(approvedBooking.getStatus()).isEqualTo(BookingStatus.APPROVED);
        verify(userRepository).findById(2L);
        verify(bookingRepository).findById(1L);
        verify(bookingRepository).save(pendingBooking);
        verify(bookingHistoryRepository).save(any(BookingHistory.class));
    }

    @Test
    void testApproveBooking_NotAdmin() {
        // Given
        User regularUser = User.builder()
                .id(2L)
                .name("Regular User")
                .role(UserRole.STUDENT)
                .build();

        when(userRepository.findById(2L)).thenReturn(Optional.of(regularUser));

        // When & Then
        assertThatThrownBy(() -> bookingService.approveBooking(1L, 2L))
                .isInstanceOf(UnauthorizedOperationException.class)
                .hasMessage("Only admins can approve bookings");

        verify(userRepository).findById(2L);
        verify(bookingRepository, never()).findById(any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void testApproveBooking_NotPendingStatus() {
        // Given
        User admin = User.builder()
                .id(2L)
                .name("Admin User")
                .role(UserRole.ADMIN)
                .build();

        Booking approvedBooking = createSampleBooking();
        approvedBooking.setStatus(BookingStatus.APPROVED);

        when(userRepository.findById(2L)).thenReturn(Optional.of(admin));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(approvedBooking));

        // When & Then
        assertThatThrownBy(() -> bookingService.approveBooking(1L, 2L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Only PENDING bookings can be approved");

        verify(userRepository).findById(2L);
        verify(bookingRepository).findById(1L);
        verify(bookingRepository, never()).save(any());
    }

    // Reject Booking Tests

    @Test
    void testRejectBooking_Success() {
        // Given
        User admin = User.builder()
                .id(2L)
                .name("Admin User")
                .role(UserRole.ADMIN)
                .build();

        Booking pendingBooking = createSampleBooking();
        pendingBooking.setStatus(BookingStatus.PENDING);

        when(userRepository.findById(2L)).thenReturn(Optional.of(admin));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(pendingBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(pendingBooking);
        when(bookingHistoryRepository.save(any(BookingHistory.class))).thenReturn(new BookingHistory());

        // When
        BookingDto rejectedBooking = bookingService.rejectBooking(1L, 2L, "Room not available");

        // Then
        assertThat(rejectedBooking.getStatus()).isEqualTo(BookingStatus.REJECTED);
        verify(userRepository).findById(2L);
        verify(bookingRepository).findById(1L);
        verify(bookingRepository).save(pendingBooking);
        verify(bookingHistoryRepository).save(any(BookingHistory.class));
    }

    // Cancel Booking Tests

    @Test
    void testCancelBooking_UserCancelsOwn_Success() {
        // Given
        Booking approvedBooking = createSampleBooking();
        approvedBooking.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(approvedBooking));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.save(any(Booking.class))).thenReturn(approvedBooking);
        when(bookingHistoryRepository.save(any(BookingHistory.class))).thenReturn(new BookingHistory());

        // When
        BookingDto cancelledBooking = bookingService.cancelBooking(1L, 1L, false);

        // Then
        assertThat(cancelledBooking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
        verify(bookingRepository).findById(1L);
        verify(userRepository).findById(1L);
        verify(bookingRepository).save(approvedBooking);
        verify(bookingHistoryRepository).save(any(BookingHistory.class));
    }

    @Test
    void testCancelBooking_AdminCancelsAny_Success() {
        // Given
        Booking approvedBooking = createSampleBooking();
        approvedBooking.setStatus(BookingStatus.APPROVED);

        User admin = User.builder()
                .id(2L)
                .name("Admin User")
                .role(UserRole.ADMIN)
                .build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(approvedBooking));
        when(userRepository.findById(2L)).thenReturn(Optional.of(admin));
        when(bookingRepository.save(any(Booking.class))).thenReturn(approvedBooking);
        when(bookingHistoryRepository.save(any(BookingHistory.class))).thenReturn(new BookingHistory());

        // When
        BookingDto cancelledBooking = bookingService.cancelBooking(1L, 2L, true);

        // Then
        assertThat(cancelledBooking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
        verify(bookingRepository).findById(1L);
        verify(userRepository).findById(2L);
        verify(bookingRepository).save(approvedBooking);
        verify(bookingHistoryRepository).save(any(BookingHistory.class));
    }

    @Test
    void testCancelBooking_UserCancelsOthers() {
        // Given
        Booking approvedBooking = createSampleBooking();
        approvedBooking.setUser(User.builder().id(2L).build()); // Different user

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(approvedBooking));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When & Then
        assertThatThrownBy(() -> bookingService.cancelBooking(1L, 1L, false))
                .isInstanceOf(UnauthorizedOperationException.class)
                .hasMessage("User can only cancel their own bookings");

        verify(bookingRepository).findById(1L);
        verify(userRepository).findById(1L);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void testCancelBooking_AlreadyCancelled() {
        // Given
        Booking cancelledBooking = createSampleBooking();
        cancelledBooking.setStatus(BookingStatus.CANCELLED);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(cancelledBooking));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When & Then
        assertThatThrownBy(() -> bookingService.cancelBooking(1L, 1L, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Booking cannot be cancelled in current status: CANCELLED");

        verify(bookingRepository).findById(1L);
        verify(userRepository).findById(1L);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void testCancelBooking_UserCancelsAfterStartTime() {
        // Given
        Booking approvedBooking = createSampleBooking();
        approvedBooking.setStatus(BookingStatus.APPROVED);
        approvedBooking.setStartTime(LocalDateTime.now().minusHours(1)); // Started in the past

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(approvedBooking));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When & Then
        assertThatThrownBy(() -> bookingService.cancelBooking(1L, 1L, false))
                .isInstanceOf(UnauthorizedOperationException.class)
                .hasMessage("Cannot cancel booking after start time");

        verify(bookingRepository).findById(1L);
        verify(userRepository).findById(1L);
        verify(bookingRepository, never()).save(any());
    }

    // Room Availability Tests

    @Test
    void testIsRoomAvailable_Success() {
        // Given
        when(bookingRepository.hasOverlappingBookings(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(false);

        // When
        boolean isAvailable = bookingService.isRoomAvailable(1L, startTime, endTime);

        // Then
        assertThat(isAvailable).isTrue();
        verify(bookingRepository).hasOverlappingBookings(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void testIsRoomAvailable_NotAvailable() {
        // Given
        when(bookingRepository.hasOverlappingBookings(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(true);

        // When
        boolean isAvailable = bookingService.isRoomAvailable(1L, startTime, endTime);

        // Then
        assertThat(isAvailable).isFalse();
        verify(bookingRepository).hasOverlappingBookings(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    // Helper method to create sample booking
    private Booking createSampleBooking() {
        return Booking.builder()
                .id(1L)
                .room(room)
                .user(user)
                .startTime(startTime)
                .endTime(endTime)
                .purpose("Study Group")
                .status(BookingStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
