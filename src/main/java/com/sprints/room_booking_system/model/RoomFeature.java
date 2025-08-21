package com.sprints.room_booking_system.model;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name="room_features")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RoomFeature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToMany(mappedBy = "features")
    private List<Room> rooms ;
}
