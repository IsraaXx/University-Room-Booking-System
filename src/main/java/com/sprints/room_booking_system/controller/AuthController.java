package com.sprints.room_booking_system.controller;

import com.sprints.room_booking_system.dto.UserDto;
import com.sprints.room_booking_system.dto.auth.AuthLoginRequest;
import com.sprints.room_booking_system.dto.auth.AuthRegisterRequest;
import com.sprints.room_booking_system.dto.auth.AuthResponse;
import com.sprints.room_booking_system.model.User;
import com.sprints.room_booking_system.security.AuthService;
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
    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody AuthRegisterRequest request) {
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
        String token = authService.login(request.getEmail(), request.getPassword());
        AuthResponse resp = AuthResponse.builder()
                .token(token)
                .message("Login successful")
                .build();
        return ResponseEntity.ok(resp);
    }
}
