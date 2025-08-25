package com.sprints.room_booking_system.service;

import com.sprints.room_booking_system.dto.BuildingDto;
import com.sprints.room_booking_system.model.Building;

import java.util.List;
import java.util.Optional;

public interface BuildingService {
    BuildingDto createBuilding(BuildingDto buildingDto);
    BuildingDto updateBuilding(Long id, BuildingDto buildingDto);
    void deleteBuilding(Long id);
    Optional<BuildingDto> getBuildingById(Long id);
    List<BuildingDto> getAllBuildings();
}
