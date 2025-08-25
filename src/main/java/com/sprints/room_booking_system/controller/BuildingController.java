package com.sprints.room_booking_system.controller;
import com.sprints.room_booking_system.dto.BuildingDto;
import com.sprints.room_booking_system.service.BuildingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/buildings")
@RequiredArgsConstructor
public class BuildingController {

    private final BuildingService buildingService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BuildingDto> createBuilding(@RequestBody BuildingDto buildingDto) {
        return ResponseEntity.ok(buildingService.createBuilding(buildingDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BuildingDto> updateBuilding(@PathVariable Long id, @RequestBody BuildingDto buildingDto) {
        return ResponseEntity.ok(buildingService.updateBuilding(id, buildingDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBuilding(@PathVariable Long id) {
        buildingService.deleteBuilding(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BuildingDto> getBuildingById(@PathVariable Long id) {
        Optional<BuildingDto> buildingDto = buildingService.getBuildingById(id);
        if (buildingDto.isPresent()) {
            return ResponseEntity.ok(buildingDto.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<BuildingDto>> getAllBuildings() {
        return ResponseEntity.ok(buildingService.getAllBuildings());
    }
}

