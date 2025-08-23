package com.sprints.room_booking_system.service.impl;

import com.sprints.room_booking_system.dto.BuildingDto;
import com.sprints.room_booking_system.model.Building;
import com.sprints.room_booking_system.repository.BuildingRepository;
import com.sprints.room_booking_system.service.BuildingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuildingServiceImpl implements BuildingService {

    private final BuildingRepository buildingRepository;

    @Override
    public Building createBuilding(BuildingDto buildingDto) {
        if (buildingRepository.existsByName(buildingDto.getName())) {
            throw new IllegalArgumentException("Building with name " + buildingDto.getName() + " already exists");
        }

        Building building = Building.builder()
                .name(buildingDto.getName())
                .location(buildingDto.getLocation())
                .build();

        return buildingRepository.save(building);
    }

    @Override
    public Building updateBuilding(Long id, BuildingDto buildingDto) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Building not found with id " + id));

        building.setName(buildingDto.getName());
        building.setLocation(buildingDto.getLocation());

        return buildingRepository.save(building);
    }

    @Override
    public void deleteBuilding(Long id) {
        if (!buildingRepository.existsById(id)) {
            throw new RuntimeException("Building not found with id " + id);
        }
        buildingRepository.deleteById(id);
    }

    @Override
    public Building getBuildingById(Long id) {
        return buildingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Building not found with id " + id));
    }

    @Override
    public List<Building> getAllBuildings() {
        return buildingRepository.findAll();
    }
}
