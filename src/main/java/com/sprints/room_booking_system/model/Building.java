package com.sprints.room_booking_system.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Entity
@Table(name="buildings")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "building Name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "location is required")
    private String location;

    @OneToMany(mappedBy = "building")
    @ToString.Exclude
    private List<Room> rooms ;
}
