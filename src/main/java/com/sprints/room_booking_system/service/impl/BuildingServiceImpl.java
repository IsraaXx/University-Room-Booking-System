package com.sprints.room_booking_system.service.impl;

import com.sprints.room_booking_system.dto.BuildingDto;
import com.sprints.room_booking_system.model.Building;
import com.sprints.room_booking_system.repository.BuildingRepository;
import com.sprints.room_booking_system.service.BuildingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BuildingServiceImpl implements BuildingService {

    private final BuildingRepository buildingRepository;

    @Override
    public BuildingDto createBuilding(BuildingDto buildingDto) {
        if (buildingRepository.existsByName(buildingDto.getName())) {
            throw new IllegalArgumentException("Building with name " + buildingDto.getName() + " already exists");
        }

        // Convert DTO to Entity
        Building building = Building.builder()
                .name(buildingDto.getName())
                .location(buildingDto.getLocation())
                .build();

        Building savedBuilding = buildingRepository.save(building);

        // Convert the saved Entity back to DTO before returning
        return toDto(savedBuilding);
    }


        @Override
        public BuildingDto updateBuilding(Long id, BuildingDto buildingDto) {
            Building existingBuilding = buildingRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Building not found with id " + id));

            // Check if name is being changed and if the new name already exists
            if (!existingBuilding.getName().equals(buildingDto.getName()) &&
                    buildingRepository.existsByName(buildingDto.getName())) {
                throw new IllegalArgumentException("Building with name " + buildingDto.getName() + " already exists");
            }

            // Update the existing entity with new data from DTO
            existingBuilding.setName(buildingDto.getName());
            existingBuilding.setLocation(buildingDto.getLocation());

            Building updatedBuilding = buildingRepository.save(existingBuilding);

            // Convert the updated Entity back to DTO
            return toDto(updatedBuilding);
        }

    @Override
    @Transactional
        public void deleteBuilding(Long id) {
            if (!buildingRepository.existsById(id)) {
                throw new IllegalArgumentException("Building not found with id " + id);
            }
            buildingRepository.deleteById(id);
        }

    @Override
    @Transactional
    public Optional<BuildingDto> getBuildingById(Long id) {
        return buildingRepository.findById(id)
                .map(this::toDto); // Convert the Entity to a DTO
    }

    @Override
    @Transactional
    public List<BuildingDto> getAllBuildings() {
        return buildingRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private BuildingDto toDto(Building building) {
        return BuildingDto.builder()
                .id(building.getId())
                .name(building.getName())
                .location(building.getLocation())
                .build();
    }
}
