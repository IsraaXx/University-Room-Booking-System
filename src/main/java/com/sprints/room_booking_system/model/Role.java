package com.sprints.room_booking_system.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Entity
@Table(name="roles")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Role Name is required")
    @Column(nullable = false)
    private String name;


    @OneToMany(mappedBy = "role")
    private List<User> users;
}
