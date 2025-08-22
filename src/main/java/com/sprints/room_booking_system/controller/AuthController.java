package com.sprints.room_booking_system.controller;

import com.sprints.room_booking_system.dto.UserDto;
import com.sprints.room_booking_system.dto.auth.AuthLoginRequest;
import com.sprints.room_booking_system.dto.auth.AuthRegisterRequest;
import com.sprints.room_booking_system.dto.auth.AuthResponse;
import com.sprints.room_booking_system.model.User;
import com.sprints.room_booking_system.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody AuthRegisterRequest request) {
        // Map to existing UserDto used by service layer
        UserDto dto = UserDto.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .role(request.getRole())
                .departmentId(request.getDepartmentId())
                .build();
        User created = userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthLoginRequest request) {
        // NOTE: Authentication/JWT issuance is not implemented in the current service layer.
        // This endpoint exists per Epic 6 and returns NOT_IMPLEMENTED until Auth/JWT is added.
        AuthResponse resp = AuthResponse.builder()
                .message("Login endpoint stubbed. Implement JWT in security layer.")
                .build();
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(resp);
    }
}
