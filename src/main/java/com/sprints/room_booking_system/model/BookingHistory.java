package com.sprints.room_booking_system.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="booking_history")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class BookingHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Action is required")
    @Pattern(
            regexp = "CREATED|APPROVED|REJECTED|CANCELLED",
            message = "Action must be one of CREATED, APPROVED, REJECTED, CANCELLED"
    )
    @Column(nullable = false)
    private String action;

    @NotNull(message = "Action time is required")
    private LocalDateTime actionTime;

    private String reason;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @PrePersist
    public void prePersist() {
        this.actionTime = LocalDateTime.now();
    }
}
