package com.sprints.room_booking_system.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuildingDto {
    private Long id;       // هيفيد في الـ update أو response
    private String name;
    private String location;
}
