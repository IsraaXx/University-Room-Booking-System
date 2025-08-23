package com.sprints.room_booking_system.controller;

import com.sprints.room_booking_system.dto.RoomDto;
import com.sprints.room_booking_system.model.Room;
import com.sprints.room_booking_system.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;

    // Admin: create room
    @PostMapping
    public ResponseEntity<Room> createRoom(@Valid @RequestBody RoomDto roomDto) {
        Room created = roomService.createRoom(roomDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Admin: update room
    @PutMapping("/{id}")
    public ResponseEntity<Room> updateRoom(@PathVariable("id") Long id, @Valid @RequestBody RoomDto roomDto) {
        Room updated = roomService.updateRoom(id, roomDto);
        return ResponseEntity.ok(updated);
    }

    // Get room by id
    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoom(@PathVariable("id") Long id) {
        Optional<Room> room = roomService.findById(id);
        return room.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // List rooms
    @GetMapping
    public ResponseEntity<List<Room>> listRooms() {
        return ResponseEntity.ok(roomService.findAllRooms());
    }

    // Admin: deactivate (soft delete) room
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable("id") Long id) {
        roomService.deactivateRoom(id);
        return ResponseEntity.noContent().build();
    }

    // Availability check (student/faculty)
    @GetMapping("/availability")
    public ResponseEntity<Boolean> isAvailable(
            @RequestParam Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime
    ) {
        boolean available = roomService.isRoomAvailable(roomId, startTime, endTime);
        return ResponseEntity.ok(available);
    }
}
