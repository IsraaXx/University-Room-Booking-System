package com.sprints.room_booking_system.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.util.List;

@Entity
@Table(name="rooms")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder

public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Room name is required")
    @Column(nullable = false)
    private String name;

    @Min(value = 1, message = "Room capacity must be at least 1")
    @Max(value = 200, message = "Room capacity must not exceed 200")
    private int capacity;

    @Column(name = "floor_number", nullable = false)
    private int floorNumber;

    @ManyToOne
    @JoinColumn(name = "building_id")
    private Building building;


    @OneToMany(mappedBy = "room")
    private List<Booking> bookings;

    @ManyToMany
    @JoinTable(
            name = "room_room_feature",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "feature_id")
    )
    private List<RoomFeature> features ;
}
