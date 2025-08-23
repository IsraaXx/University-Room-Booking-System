package com.sprints.room_booking_system.service;

import com.sprints.room_booking_system.dto.BuildingDto;
import com.sprints.room_booking_system.model.Building;

import java.util.List;

public interface BuildingService {
    Building createBuilding(BuildingDto buildingDto);
    Building updateBuilding(Long id, BuildingDto buildingDto);
    void deleteBuilding(Long id);
    Building getBuildingById(Long id);
    List<Building> getAllBuildings();
}
